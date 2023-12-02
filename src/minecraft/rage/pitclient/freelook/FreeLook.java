package rage.pitclient.freelook;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.InputEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;

public class FreeLook {
	private Minecraft mc = Minecraft.getMinecraft();
  
	private KeyBinding keyBinding;
  
	public boolean cameraToggled = false;
  
	public float cameraYaw;
  
	public float cameraPitch;
  
	private int previousPerspective = 0;
  
	
	public FreeLook() {
    	this.keyBinding = new KeyBinding("Free Look", Keyboard.KEY_LMENU, "PitMod");
    	PitClient.registerKeyBinding(this.keyBinding);
	}
	
	@SubscribeEvent
	public void onKey(InputEvent.KeyInputEvent event) {
		if (!mc.inGameHasFocus || !Display.isActive() && cameraToggled) {
			mc.gameSettings.thirdPersonView = 0;
		}
		if (Keyboard.getEventKey() == keyBinding.getKeyCode()) {
			cameraToggled = !cameraToggled;
			cameraYaw = mc.getRenderViewEntity().rotationYaw;
			cameraPitch = mc.getRenderViewEntity().rotationPitch;
			if (cameraToggled) {
				previousPerspective = mc.gameSettings.thirdPersonView;
				mc.gameSettings.thirdPersonView = 1;
			} else {
				mc.gameSettings.thirdPersonView = previousPerspective;
			} 
		}
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (!cameraToggled)
			return;
		if (mc.currentScreen != null) {
			cameraToggled = false;
			mc.gameSettings.thirdPersonView = 0;
		}
	}
	
	public float getYaw() {
		return cameraToggled ? cameraYaw : mc.thePlayer.rotationYaw;
	}
	
	public float getPitch() {
		return cameraToggled ? cameraPitch : mc.thePlayer.rotationPitch;
	}
	
	public boolean overrideMouse() {
		if (mc.inGameHasFocus && Display.isActive()) {
			if (!cameraToggled) {
				return true;
			}
			mc.mouseHelper.mouseXYChange();
			float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f2 = f1 * f1 * f1 * 8.0F;
			float f3 = (float) mc.mouseHelper.deltaX * f2;
			float f4 = (float) mc.mouseHelper.deltaY * f2;
			
			cameraYaw += f3 * 0.15F;
			cameraPitch += f4 * 0.15F;
			
			if (cameraPitch > 90) cameraPitch = 90;
			if (cameraPitch < -90) cameraPitch = -90;
		}
		return false;
	}
}