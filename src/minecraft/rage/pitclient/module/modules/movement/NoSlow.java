package rage.pitclient.module.modules.movement;

import net.minecraft.item.ItemSword;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.MotionEvent;
import rage.pitclient.events.NoSlowdownEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "No Slowdown", category = Category.MOVEMENT)
public class NoSlow extends Module {

	private Setting strafe = regBooleanTip("Strafe", true, "Left/Right movements");
	private Setting forward = regBooleanTip("Forward", true, "Forward/Back movements");
	private Setting alwaysSprint = regBoolean("Always sprint", true);
	private Setting onlyWhenJumping = regBooleanTip("Only when jumping", true, "Only active when in the air");
	
	@SubscribeEvent
	public void onMotion(MotionEvent event) {
		if (!enabled)
			return;
		if (mc.theWorld == null)
			return;
//		if ((isHoldingSword() && Mouse.isButtonDown(1)) | mc.thePlayer.isBlocking() && (mc.thePlayer.getLastAttackerTime()!= mc.thePlayer.ticksExisted) && (mc.thePlayer.movementInput.moveForward != 0.0f || mc.thePlayer.movementInput.moveStrafe != 0.0f)) {
//			if (event.getEventState() == EventState.PRE) {
//        		mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-.1F, -.6F, -.1F), EnumFacing.DOWN));
//			} else {
//				mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
//			}
//		}
	}
	
    private boolean isHoldingSword() {
        return mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

	@SubscribeEvent
	public void onNoSlow(NoSlowdownEvent event) {
		if (!enabled)
			return;
		
		if (onlyWhenJumping.getValBoolean() && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) return;
		
		if (strafe.getValBoolean()) event.strafe = 1.0f;
		if (forward.getValBoolean()) event.forward = 1.0f;
		
		if (alwaysSprint.getValBoolean()) mc.thePlayer.setSprinting(true);
	}
}
