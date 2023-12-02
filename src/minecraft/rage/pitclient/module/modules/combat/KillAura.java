package rage.pitclient.module.modules.combat;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.EventPriority;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.LivingEvent.LivingUpdateEvent;
import rage.pitclient.events.MotionEvent;
import rage.pitclient.events.RenderGameOverlayEvent;
import rage.pitclient.events.RenderGameOverlayEvent.ElementType;
import rage.pitclient.events.RenderLivingEvent;
import rage.pitclient.events.RenderWorldLastEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.module.modules.visual.Nametags;
import rage.pitclient.module.modules.visual.Tracers;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.KillAuraTarget;
import rage.pitclient.util.UnicodeFontRenderer;

@ModuleInfo(name = "Kill Aura", tooltip = "Automatically attacks players", category = Category.COMBAT)
public class KillAura extends Module {

	private Setting maxDistance = regSlider("Distance", 3.5, 3, 4.5, false);
	private Setting fov = regSlider("FOV", 80, 0, 360, true);
	private Setting minCps = regSlider("Min CPS", 10, 2, 20, true);
	private Setting maxCps = regSlider("Max CPS", 12, 2, 20, true);
	private Setting sortingOption = regOptionTip("Sorting", "Determines which entity in the fov will be attacked", "FOV", "Lowest Health", "Highest Health", "Closest");
	private Setting fovOption = regOptionTip("FOV Point", "Determines which point on the target FOV will be calculated from", "Eyes", "Chest");
	private Setting fovCalc = regOptionTip("FOV Calculation", "3D accounts for vertical angle", "2D", "3D");
	private Setting switchMode = regOptionTip("Mode", "Switch", "Switch limits how fast a new target will be chosen", "Switch", "Instant");
	private Setting switchDelay = regSliderTip("Switch Delay", 400, 100, 1000, true, "Minimum time in milliseconds that a new target will be chosen");
	private Setting snapSpeed = regSliderTip("Head Snap Speed", 30, 10, 50, true, "Speed at which you will lock to target. Measured in angle/tick");
	private Setting lockView = regBoolean("Lock View", false);
	private Setting showTarget = regBoolean("Show Target", true);
	private Setting requireMouseDown = regBoolean("Require Mouse Down", true);
	private Setting autoblock = regBooleanTip("Autoblock", false, "Broken rn");
	private Setting dirtyMode = regBooleanTip("Dirty Mode", false, "Prioritizes by Lowest Health if below threshold");
	private Setting dirtyHealth = regSlider("Dirty Health", 4, 1, 20, true);
	private Setting targetHud = regBoolean("Target HUD", true);
	
	// mjuventus@eosc.edu:Bonavita2016!
		
	private Random r = new Random();
	private double nextClick;
	private double nextSwitch;
	public EntityPlayer currentTarget;
	private boolean blocking;
	private ArrayList<KillAuraTarget> kaTargets = new ArrayList<>();
	
	@SubscribeEvent
	public void onMove(MotionEvent.Post event) {
		if (!enabled)
			return;
		
		if (currentTarget != null) {
			if ((Mouse.isButtonDown(1) | autoblock.getValBoolean()) && mc.objectMouseOver != null) {
				block();
			}
		}
	}
	
	@SubscribeEvent
	public void onMove(MotionEvent.Pre event) {
		if (!enabled)
			return;
		if (mc.currentScreen != null && requireMouseDown.getValBoolean())
			return;	
		
		if (currentTarget != null && Mouse.isButtonDown(0)) {
            double speed = snapSpeed.getValDouble();
			float[] rotations = getRotations(currentTarget, (float)speed);

			if (lockView.getValBoolean()) {
				mc.thePlayer.rotationYaw = rotations[0];
				mc.thePlayer.rotationPitch = rotations[1];
			}
			
            event.yaw = rotations[0];
            event.pitch = rotations[1];
		}
	}
	
    private float[] getRotations(EntityLivingBase entityIn, float speed) {
        float yaw = updateRotation(mc.thePlayer.rotationYaw,
                getNeededRotations(entityIn)[0],
                speed);
        float pitch = updateRotation(mc.thePlayer.rotationPitch,
                getNeededRotations(entityIn)[1],
                speed);
        return new float[]{yaw, pitch};
    }
    
    private float updateRotation(float currentRotation, float intendedRotation, float increment) {
        float f = MathHelper.wrapAngleTo180_float(intendedRotation - currentRotation);

        if (f > increment)
            f = increment;

        if (f < -increment)
            f = -increment;

        return currentRotation + f;
    }
    
    private float[] getNeededRotations(EntityLivingBase entityIn) {
        double d0 = entityIn.posX - mc.thePlayer.posX;
        double d1 = entityIn.posZ - mc.thePlayer.posZ;
        double d2 = entityIn.posY + entityIn.getEyeHeight() - (mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight());

        double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f = (float) (MathHelper.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(d2, d3) * 180.0D / Math.PI));
        return new float[]{f, f1};
    }
	
	@SubscribeEvent
	public void onTick(LivingUpdateEvent event) {
		if (!enabled)
			return;
		if (mc.theWorld == null)
			return;
		if (mc.currentScreen != null && requireMouseDown.getValBoolean())
			return;	
		if (mc.getRenderViewEntity() == null)
			return;

		getTargets();
		
		if (switchMode.getValString().equalsIgnoreCase("Switch")) {
			double current = (double) System.currentTimeMillis();
			if (current > nextSwitch) {
				sortTargets();
			}
		} else sortTargets();
		
        if (!isHoldingSword()) blocking = false;

        if (currentTarget == null) {
        	unblock();
        	return;
        }
        
        double distance = maxDistance.getValDouble();
        
    	Vec3 eyePos = mc.getRenderViewEntity().getPositionEyes(1.0F);
    	Vec3 lookVec = mc.getRenderViewEntity().getLook(1.0F);
    	Vec3 addLookVec = eyePos.addVector(lookVec.xCoord * distance, lookVec.yCoord * distance, lookVec.zCoord * distance);
    	float collision = currentTarget.getCollisionBorderSize();
    	AxisAlignedBB collisionBox = currentTarget.getEntityBoundingBox().expand((double)collision, (double)collision, (double)collision);
    	MovingObjectPosition hitObject = collisionBox.calculateIntercept(eyePos, addLookVec);
    	mc.objectMouseOver = hitObject;
        
		if (Mouse.isButtonDown(0) | !requireMouseDown.getValBoolean()) {
			double current = (double) System.currentTimeMillis();
			
			if (current > nextClick) {
				double n = r.nextGaussian();
				double min = minCps.getValDouble();
				double max = maxCps.getValDouble();
				
				double v = (max - min)/2;
				double m = (max + min)/2;
				
				double cps = n * Math.sqrt(v) + m;
				
				double next = 1000/cps;

				nextClick = current + next;
		        
		        if (mc.thePlayer.isBlocking() | blocking) {
		        	unblock();
		        }
		        
		        mc.thePlayer.swingItem();
		        mc.playerController.attackEntity(mc.thePlayer, currentTarget);
		        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
		        if ((Mouse.isButtonDown(1) | autoblock.getValBoolean()) && isHoldingSword()) {
		        	block();
		        }
		        
			}
		}
	}
	
	private int enemyIndex;
	
	private void sortTargets() {
        if (kaTargets.size() == 0) return;
        
        if (dirtyMode.getValBoolean() && mc.thePlayer.getHealth() <= dirtyHealth.getValDouble()) {
        	kaTargets.sort(Comparator.comparing(e -> e.entity.getHealth()));
        	currentTarget = kaTargets.get(0).entity;
        	return;
        }
        
        EntityPlayer oldTarget = currentTarget;
        enemyIndex = -1;
        kaTargets.forEach(target -> {
        	if (PitClient.friendManager.isEnemy(EnumChatFormatting.getTextWithoutFormattingCodes(target.entity.getName()))) enemyIndex = kaTargets.indexOf(target);
        });

        if (enemyIndex != -1) {
        	currentTarget = kaTargets.get(enemyIndex).entity;
        	return;
        }
        
        switch(sortingOption.getValString().toLowerCase()) {
        case "fov":
        	kaTargets.sort(Comparator.comparingDouble(e -> e.fov));
        	currentTarget = kaTargets.get(0).entity;
        	break;
        case "lowest health":
        	kaTargets.sort(Comparator.comparing(e -> e.entity.getHealth()));
        	currentTarget = kaTargets.get(0).entity;
        	break;
        case "highest health":
        	kaTargets.sort(Comparator.comparing(e -> e.entity.getHealth()));
        	Collections.reverse(kaTargets);
        	currentTarget = kaTargets.get(0).entity;
        	break;
        case "closest":
        	kaTargets.sort(Comparator.comparing(e -> mc.thePlayer.getDistanceToEntity(e.entity)));
        	currentTarget = kaTargets.get(0).entity;
        	break;
        default:
        	break;
        }
        
        if (oldTarget != currentTarget) nextSwitch = System.currentTimeMillis() + getInternalSetting("Switch Delay").getValDouble();
	}

	private void getTargets() {
		Vec3 posVec = mc.getRenderViewEntity().getPositionVector();
		Vec3 lookVec = mc.getRenderViewEntity().getLook(1.0f);
		double distance = maxDistance.getValDouble();
		
        List<Entity> entityList = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().expand(distance,distance,distance));
        
        kaTargets.clear();
        
        for (Entity e : entityList) {
        	if (!(e instanceof EntityPlayer)) continue;
        	if (mc.thePlayer.getDistanceToEntity(e) > distance) continue;
        	if (PitClient.scoreboardManager.npcList.contains(e.getName())) continue;
        	if (PitClient.scoreboardManager.wdrList.contains(e.getName())) continue;
        	
        	Vec3 distanceVec;
        	if (fovOption.getValString().equalsIgnoreCase("chest")) {
        		distanceVec = e.getPositionVector().subtract(0, 0.4, 0).subtract(posVec).normalize();
        	} else distanceVec = e.getPositionVector().subtract(posVec).normalize();
        	
        	if (fovCalc.getValString().equalsIgnoreCase("2D")) {
        		posVec = new Vec3(posVec.xCoord, 0, posVec.zCoord);
        		lookVec = new Vec3(lookVec.xCoord, 0, lookVec.zCoord);
        		distanceVec = new Vec3(e.getPositionVector().xCoord, 0, e.getPositionVector().zCoord).subtract(posVec).normalize();
        	}
        	
        	EntityPlayer ent = (EntityPlayer) e;
        	
        	double fovToEnt = Math.toDegrees(Math.acos(distanceVec.dotProduct(lookVec.normalize())));
        	
        	double maxFov = fov.getValDouble();
        	if (fovToEnt > maxFov) continue;
        	if (PitClient.friendManager.isFriend(EnumChatFormatting.getTextWithoutFormattingCodes(ent.getName()))) continue;
        	kaTargets.add(new KillAuraTarget(ent, fovToEnt));
        }
        
        if (kaTargets.size() == 0) currentTarget = null;
	}
	
	private void block() {
		if (blocking | !Mouse.isButtonDown(1)) return;
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
		blocking = true;

	}
	
	private void unblock() {
		if (!blocking) return;
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM,BlockPos.ORIGIN,EnumFacing.DOWN));
        blocking = false;
	}
	
    private boolean isHoldingSword() {
        return mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }
	
	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent.Pre event) {
		double min = minCps.getValDouble();
		double max = maxCps.getValDouble();
		
		if (min > max) {
			minCps.setValDouble(max);
		}
	}
    
	private UnicodeFontRenderer targetRenderer = new UnicodeFontRenderer(new Font("Trebuchet MS", Font.BOLD, 17));
	private boolean CANCEL_NAME;
	
	@SubscribeEvent
	public void onRenderPlayer(RenderGameOverlayEvent.Post event) {
		if (!enabled)
			return;
		if (event.type != ElementType.TEXT)
			return;
		if (currentTarget == null)
			return;
		if (mc.theWorld == null)
			return;
		if (!targetHud.getValBoolean())
			return;
		if (mc.currentScreen != null && requireMouseDown.getValBoolean())
			return;
		if (!Mouse.isButtonDown(0) && requireMouseDown.getValBoolean())
			return;
		ScaledResolution sr = new ScaledResolution(mc);
		
		int startX = 20;
		int renderX = (sr.getScaledWidth() / 2) + startX;
		int renderY = (sr.getScaledHeight() / 2) + 10;
		int maxX2 = 30;
		if (currentTarget.getCurrentArmor(3) != null) {
			maxX2 += 15;
		}
		if (currentTarget.getCurrentArmor(2) != null) {
			maxX2 += 15;
		}
		if (currentTarget.getCurrentArmor(1) != null) {
			maxX2 += 15;
		}
		if (currentTarget.getCurrentArmor(0) != null) {
			maxX2 += 15;
		}
		if (currentTarget.getHeldItem() != null) {
			maxX2 += 15;
		}
		String name = EnumChatFormatting.getTextWithoutFormattingCodes(currentTarget.getName());
    	float health = currentTarget.getHealth()/2;
    	health = (float) (Math.floor(health * 10) / 10);
		float percent = currentTarget.getHealth()/currentTarget.getMaxHealth();
    	int color = 0xf0ff5555;
    	if (health > 3.0) color = 0xf0deda16;
    	if (health > 7.0) color = 0xf007ab07;
		
		
		float maxX = Math.max(maxX2, mc.fontRendererObj.getStringWidth(name) + 30);
		Gui.drawRect(renderX, renderY, (int) (renderX + maxX), renderY + 40, new Color(0, 0, 0, 0.6f).getRGB());
		Gui.drawRect(renderX, renderY + 38, (int) (renderX + (maxX * percent)), renderY + 40, color);
		mc.fontRendererObj.drawStringWithShadow(name, renderX + 25, renderY + 7, -1);
		int xAdd = 0;
		double multiplier = 0.85;
		GlStateManager.pushMatrix();
		GlStateManager.scale(multiplier, multiplier, multiplier);
		if (currentTarget.getCurrentArmor(3) != null) {
			mc.getRenderItem().renderItemAndEffectIntoGUI(currentTarget.getCurrentArmor(3), (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier), (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
			xAdd += 15;
		}
		if (currentTarget.getCurrentArmor(2) != null) {
			mc.getRenderItem().renderItemAndEffectIntoGUI(currentTarget.getCurrentArmor(2), (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier), (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
			xAdd += 15;
		}
		if (currentTarget.getCurrentArmor(1) != null) {
			mc.getRenderItem().renderItemAndEffectIntoGUI(currentTarget.getCurrentArmor(1), (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier), (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
			xAdd += 15;
		}
		if (currentTarget.getCurrentArmor(0) != null) {
			mc.getRenderItem().renderItemAndEffectIntoGUI(currentTarget.getCurrentArmor(0), (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier), (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
			xAdd += 15;
		}
		if (currentTarget.getHeldItem() != null) {
			mc.getRenderItem().renderItemAndEffectIntoGUI(currentTarget.getHeldItem(), (int) ((((sr.getScaledWidth() / 2) + startX + 23) + xAdd) / multiplier), (int) (((sr.getScaledHeight() / 2) + 28) / multiplier));
		}
		GlStateManager.popMatrix();
		
		CANCEL_NAME = true;
		boolean flag1 = PitClient.moduleManager.getModuleByClass(Nametags.class).isEnabled();
		boolean flag2 = PitClient.moduleManager.getModuleByClass(Tracers.class).isEnabled();

		if (flag1) 	PitClient.moduleManager.getModuleByClass(Nametags.class).setEnabled(false);
		if (flag2) 	PitClient.moduleManager.getModuleByClass(Tracers.class).setEnabled(false);
		
		GuiInventory.drawEntityOnScreen((int)renderX + 12, (int)renderY + 33, 15, currentTarget.rotationYaw, currentTarget.rotationPitch, currentTarget);

		if (flag1) 	PitClient.moduleManager.getModuleByClass(Nametags.class).setEnabled(true);
		if (flag2) 	PitClient.moduleManager.getModuleByClass(Tracers.class).setEnabled(true);
		
		CANCEL_NAME = false;
		
		
//		ScaledResolution sr = new ScaledResolution(mc);
//		int height = sr.getScaledHeight()/2;
//		int width = sr.getScaledWidth()/2;
//		int box_len = 88;
//		int box_hei = 30;
//
//		int left = width - box_len/2;
//		int top = height + 40;
//		int right = width + box_len/2;
//		int bottom = height + box_hei + 40;
//		
//		Gui.drawRect(left-30, top-25, right+5, bottom+5, 0xd0101010);
//		
//		Gui.drawRect(left, top, right, bottom, 0xd01a1a1a);
//		
//		Gui.drawRect(left + box_len/5 + 10, top + box_hei/2 + 5, right - box_len/5 + 10, bottom - 5, 0xddff5555);
//		
//    	float health = currentTarget.getHealth()/2;
//    	health = (float) (Math.floor(health * 10) / 10);
//		
//    	int color = 0xf0ff5555;
//    	if (health > 3.0) color = 0xf0deda16;
//    	if (health > 7.0) color = 0xf007ab07;
//    	
//    	String t = Float.toString(health);
//
//		targetRenderer.drawString(t, left + 5, bottom - 5 - 8, color);
//		float percent = currentTarget.getHealth()/currentTarget.getMaxHealth();
//
//		Gui.drawRect(left + box_len/5 + 10, top + box_hei/2 + 5, (int) (left + box_len/5 + 10 + 54*percent), bottom - 5, color);
//		for (int i=0;i<=3;i++) {
//			Gui.drawRect(left + 2 + 22*i, top - 18, left + 10 + 8 + 22*i, top - 2, 0xd01a1a1a);
//			mc.getRenderItem().renderItemAndEffectIntoGUI(currentTarget.getCurrentArmor(i), left+ 2 + 22*i, top - 18);
//		}
//		CANCEL_NAME = true;
//		boolean flag1 = PitMod.moduleManager.getModuleByClass(Nametags.class).isEnabled();
//		boolean flag2 = PitMod.moduleManager.getModuleByClass(Tracers.class).isEnabled();
//
//		if (flag1) 	PitMod.moduleManager.getModuleByClass(Nametags.class).setEnabled(false);
//		if (flag2) 	PitMod.moduleManager.getModuleByClass(Tracers.class).setEnabled(false);
//		
//		GuiInventory.drawEntityOnScreen(left - 14, top + 25, 20, currentTarget.rotationYaw, currentTarget.rotationPitch, currentTarget);
//
//		if (flag1) 	PitMod.moduleManager.getModuleByClass(Nametags.class).setEnabled(true);
//		if (flag2) 	PitMod.moduleManager.getModuleByClass(Tracers.class).setEnabled(true);
//		
//		CANCEL_NAME = false;
//		int x = left+box_len/2;
//		int y = top + 5;
//		String text = EnumChatFormatting.getTextWithoutFormattingCodes(currentTarget.getName());
//		int formattedx = x - targetRenderer.getStringWidth(text) / 2;
//		targetRenderer.drawString(text, formattedx, y, 0xffefefef);
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onRenderNameTag(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
		if (!enabled)
			return;
		if (CANCEL_NAME) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onRenderOverlay(RenderWorldLastEvent event) {
		if (!enabled)
			return;
		if (!showTarget.getValBoolean())
			return;
		if (mc.theWorld == null)
			return;
		if (currentTarget == null)
			return;
		if (mc.currentScreen != null && requireMouseDown.getValBoolean())
			return;
		if (!Mouse.isButtonDown(0) && requireMouseDown.getValBoolean())
			return;
		
        GlStateManager.disableTexture2D();
        
        GlStateManager.color(1.0f,
        		0f,
        		0f,
        		1f);
        
        double x = (currentTarget.lastTickPosX + (currentTarget.posX - currentTarget.lastTickPosX) * (double)event.partialTicks) - mc.getRenderManager().viewerPosX;
        double y = (currentTarget.lastTickPosY + (currentTarget.posY - currentTarget.lastTickPosY) * (double)event.partialTicks) - mc.getRenderManager().viewerPosY;
        double z = (currentTarget.lastTickPosZ + (currentTarget.posZ - currentTarget.lastTickPosZ) * (double)event.partialTicks) - mc.getRenderManager().viewerPosZ;

		AxisAlignedBB axisalignedbb = currentTarget.getEntityBoundingBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - 0.2 - currentTarget.posX + x, axisalignedbb.minY - currentTarget.posY + y, axisalignedbb.minZ - 0.2 - currentTarget.posZ + z, axisalignedbb.maxX + 0.2 - currentTarget.posX + x, axisalignedbb.maxY + 0.2 - currentTarget.posY + y, axisalignedbb.maxZ + 0.2 - currentTarget.posZ + z);
		RenderGlobal.drawSelectionBoundingBox(axisalignedbb1);
		
        GlStateManager.enableTexture2D();
	}
}
