package rage.pitclient.module.modules.movement;

import net.minecraft.client.settings.KeyBinding;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.MotionEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Bhop", category = Category.MOVEMENT, permission = 4)
public class Bhop extends Module {

	private Setting speedSetting = regSlider("Speed", 0.025, 0.0001, 0.04, false);
	
	private double speed;
	
	@SubscribeEvent
	public void onMotionEvent(MotionEvent.Pre event) {

		if (!enabled) return;
		
		if (isMoving()) {

			//mc.gameSettings.keyBindJump.pressed = false;
			
			if (isOnGround(0.00001)) {
				//boosted = true;
				speed = speedSetting.getValDouble() * 13.5;
				mc.thePlayer.jump();
			}
			
			strafe(((float)speed));
			
			speed -= speed/19.5;
			
		}
		
	}
	
    public double getDirection() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if(mc.thePlayer.moveForward < 0F)
            rotationYaw += 180F;

        float forward = 1F;
        if(mc.thePlayer.moveForward < 0F)
            forward = -0.5F;
        else if(mc.thePlayer.moveForward > 0F)
            forward = 0.5F;

        if(mc.thePlayer.moveStrafing > 0F)
            rotationYaw -= 90F * forward;

        if(mc.thePlayer.moveStrafing < 0F)
            rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }
    
	
    public void strafe(final float speed) {

        final double yaw = getDirection();
        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }
	
    public boolean isOnGround(double height) {
        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) return false;
        return true;
    }
	
    public boolean isMoving() {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }
	
}
