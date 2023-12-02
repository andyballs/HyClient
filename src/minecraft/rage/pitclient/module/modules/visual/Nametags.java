package rage.pitclient.module.modules.visual;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.EntityViewRenderEvent;
import rage.pitclient.events.RenderLivingEvent;
import rage.pitclient.events.RenderPlayerEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.RenderUtils;

@ModuleInfo(name = "Nametags", category = Category.VISUAL)
public class Nametags extends Module {

	private Setting playerSetting = regOptionTip("Render Names", "Determines what players will be have nametags", "All Players", "Warning List", "Warning and Darks");
	private Setting scale = regSlider("Nametags Scale", 1, 0.2, 3, false);
	private Setting maxDistance = regSliderTip("Distance to draw", 256, 1, 256, true, "Players above this distance away won't have nametags");
	private Setting drawHealth = regBooleanTip("Draw Health", false, "Displays the players health as well");
	private Setting drawInvisibles = regBooleanTip("Invisibles", false, "Displays nametags for invisible entities");

	@SubscribeEvent
	public void onRenderNameTag(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
		if (!enabled)
			return;
		if (event.entity == null | !(event.entity instanceof EntityPlayer))
			return;
		if (event.entity.getName().equals(Minecraft.getMinecraft().thePlayer.getName()))
			return;
		if (!renderName(event.entity.getName()))
			return;
		if (!doRenderDistance(event.entity))
			return;
		event.setCanceled(true);		
	}
	
	@SubscribeEvent
	public void onRender(RenderPlayerEvent.Post event) {
		if (!enabled)
			return;
		if (event.entityPlayer == null | !(event.entityPlayer instanceof EntityPlayer))
			return;
		if (event.entityPlayer.getName().equals(Minecraft.getMinecraft().thePlayer.getName()))
			return;
		if (!renderName(event.entityPlayer.getName()))
			return;
		if (!doRenderDistance(event.entityPlayer))
			return;
		if (!drawInvisibles.getValBoolean() && event.entityPlayer.isInvisibleToPlayer(mc.thePlayer))
			return;
		
        double x = (event.entityPlayer.lastTickPosX + (event.entityPlayer.posX - event.entityPlayer.lastTickPosX) * (double)event.partialRenderTick) - event.renderer.getRenderManager().viewerPosX;
        double y = (event.entityPlayer.lastTickPosY + (event.entityPlayer.posY - event.entityPlayer.lastTickPosY) * (double)event.partialRenderTick) - event.renderer.getRenderManager().viewerPosY;
        double z = (event.entityPlayer.lastTickPosZ + (event.entityPlayer.posZ - event.entityPlayer.lastTickPosZ) * (double)event.partialRenderTick) - event.renderer.getRenderManager().viewerPosZ;

        renderNametag(event.entityPlayer, (float) x, (float) y, (float) z);
	}
	
	@SubscribeEvent
	public void onFogRender(EntityViewRenderEvent.FogDensity event) {
		if (!enabled)
			return;
		event.density = 0f;
		event.setCanceled(true);
	}
	
	private void renderNametag(EntityPlayer player, float x, float y, float z) {
        y += (float)(1.55 + (player.isSneaking() ? 0.5 : 0.7));
        
        String str = player.getDisplayName().getFormattedText();
        
        if (PitClient.friendManager.isFriend(EnumChatFormatting.getTextWithoutFormattingCodes(player.getName()))) {
        	str = EnumChatFormatting.BLUE + EnumChatFormatting.getTextWithoutFormattingCodes(player.getName());
        }
        
        if (PitClient.friendManager.isEnemy(EnumChatFormatting.getTextWithoutFormattingCodes(player.getName()))) {
        	str = EnumChatFormatting.DARK_RED + EnumChatFormatting.getTextWithoutFormattingCodes(player.getName());
        }

        if (drawHealth.getValBoolean()) {
        	
        	float health = player.getHealth()/2;
        	health = health*10;
        	health = (float)((int) health);
        	health = health /10;
        	
        	EnumChatFormatting color = EnumChatFormatting.DARK_RED;
        	
        	if (health > 3.0) color = EnumChatFormatting.YELLOW;
        	if (health > 7.0) color = EnumChatFormatting.GREEN;
        	
        	str += " " + color + health;
        }
        
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
        
        double mult = scale.getValDouble();
        double size = (getSize(player) / 10.0f * 6 * 1.5) * mult;
        
        GL11.glPushMatrix();
        RenderUtils.startDrawing();
        GL11.glTranslatef(x, y, z);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glScaled(-0.01666666753590107 * size, -0.01666666753590107 * size, 0.01666666753590107 * size);
        
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        int i = -10;
        int j = fontrenderer.getStringWidth(str) / 2;
        
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double)(-j - 1), (double)(-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double)(-j - 1), (double)(8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double)(j + 1), (double)(8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double)(j + 1), (double)(-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127);
        GlStateManager.depthMask(true);
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        
        RenderUtils.stopDrawing();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
    
    private float getSize(final EntityPlayer player) {
        return (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player) / 4.0f <= 2.0f) ? 2.0f : (Minecraft.getMinecraft().thePlayer.getDistanceToEntity(player) / 4.0f);
    }
    
	private boolean doRenderDistance(Entity player) {
		return mc.thePlayer.getDistanceToEntity(player) <= maxDistance.getValDouble();
	}
	
	private boolean renderName(String player) {
		if (PitClient.scoreboardManager.npcList.contains(player)) return false;
		
		if (PitClient.friendManager.isEnemy(player)) return true;
		if (PitClient.friendManager.isFriend(player)) return true;
		
		switch (playerSetting.getValString()) {
		case "All Players":
			return true;
		case "Warning List":
			return PitClient.getInstance().getManager().currentPlayerMap.get(player) != null;
		case "Warning and Darks":
			return PitClient.getInstance().getManager().currentPlayerMap.get(player) != null | 
					PitClient.getInstance().getManager().darkMap.get(player) != null;
		default:
			return false;
		}
	}
	
}
