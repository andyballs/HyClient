package rage.pitclient.module.modules.combat;

import java.util.Random;

import org.lwjgl.input.Mouse;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.LivingEvent;
import rage.pitclient.events.RenderGameOverlayEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Auto Clicker", category = Category.COMBAT)
public class AutoClicker extends Module {
	
	private Setting minLeft = regSlider("Min CPS", 10, 2, 20, true);
	private Setting maxLeft = regSlider("Max CPS", 12, 2, 20, true);
	private Setting blockChance = regSlider("Block Hit Chance", 50, 0, 100, true);
	private Setting breakBlocks = regBoolean("Break Blocks", false);
	private Setting rightActive = regBoolean("Right Click", false);
	private Setting minRight = regSlider("Min Right CPS", 10, 2, 30, true);
	private Setting maxRight = regSlider("Max Right CPS", 12, 2, 30, true);
	private Setting cake = regBooleanTip("Cake Mode", false, "200 CPS right click, must have Right Click enabled");
	private Setting blocksOnly = regBoolean("Blocks Only", true);

	private Random r = new Random();
	
	private double nextLeftClick;
	private double nextRightClick;
	
	@SubscribeEvent
	public void onTick(LivingEvent.LivingUpdateEvent event) {
		if (!enabled)
			return;
		if (mc.theWorld == null)
			return;
		if (mc.currentScreen != null)
			return;

		if (Mouse.isButtonDown(0)) {
			double current = (double) System.currentTimeMillis();
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
			if (current > nextLeftClick) {
				
				if (Mouse.isButtonDown(1)) {
					if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
						double chance = blockChance.getValDouble();
						
						if (r.nextDouble() <= chance/100) {
							int rightCode = mc.gameSettings.keyBindUseItem.getKeyCode();
							KeyBinding.setKeyBindState(rightCode, true);
							KeyBinding.onTick(rightCode);
						}
					}
				}
				
				double n = r.nextGaussian();
				double min = minLeft.getValDouble();
				double max = maxLeft.getValDouble();
				
				double v = (max - min)/2;
				double m = (max + min)/2;
				
				double cps = n * Math.sqrt(v) + m;
				
				double next = 1000/cps;

				nextLeftClick = current + next;
				int leftCode = mc.gameSettings.keyBindAttack.getKeyCode();
				KeyBinding.setKeyBindState(leftCode, true);

				switch(mc.objectMouseOver.typeOfHit) {
					case ENTITY:
						mc.thePlayer.swingItem();
						mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
						break;
					case MISS:
						mc.thePlayer.swingItem();
						break;
					case BLOCK:
						boolean block = breakBlocks.getValBoolean();
						if (block) {
							mc.thePlayer.swingItem();
						} else {
							KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
							mc.thePlayer.swingItem();
						}
						break;
					default:
						break;						
				}
			}
		}
		
		if (Mouse.isButtonDown(1) && rightActive.getValBoolean()) {
			if (mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) && blocksOnly.getValBoolean()) {
				return;
			}
			double current = (double) System.currentTimeMillis();

			if (current > nextRightClick) {
				double n = r.nextGaussian();
				double min = minRight.getValDouble();
				double max = maxRight.getValDouble();
				
				if (cake.getValBoolean()) {
					min = 200;
					max = 200;
				}
				
				double v = (max - min)/2;
				double m = (max + min)/2;
				
				double cps = n * Math.sqrt(v) + m;
				
				double next = 1000/cps;

				nextRightClick = current + next;
				int rightCode = mc.gameSettings.keyBindUseItem.getKeyCode();
				KeyBinding.setKeyBindState(rightCode, true);
				KeyBinding.onTick(rightCode);
				//mc.rightClickMouse();
			}
		}
	}
	
	
	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent.Pre event) {
		if (!enabled)
			return;
		
		double min = minLeft.getValDouble();
		double max = maxLeft.getValDouble();
		
		if (min > max) {
			minLeft.setValDouble(max);
		}
		min = minRight.getValDouble();
		max = maxRight.getValDouble();
		
		if (min > max) {
			minRight.setValDouble(max);
		}
	}
	
}
