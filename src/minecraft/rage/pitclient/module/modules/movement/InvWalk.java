package rage.pitclient.module.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.LivingEvent.LivingUpdateEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;

@ModuleInfo(name = "Inventory Walk", tooltip = "Player can move in gui's", category = Category.MOVEMENT)
public class InvWalk extends Module {

	@SubscribeEvent
	public void onTick(LivingUpdateEvent event) {
		if (!enabled)
			return;
		
		int w = mc.gameSettings.keyBindForward.getKeyCode();
		int a = mc.gameSettings.keyBindLeft.getKeyCode();
		int s = mc.gameSettings.keyBindBack.getKeyCode();
		int d = mc.gameSettings.keyBindRight.getKeyCode();
		int jump = mc.gameSettings.keyBindJump.getKeyCode();
		
		int[] keyList = { w, a, s, d, jump };
		
		if (mc.currentScreen != null && !mc.ingameGUI.getChatGUI().getChatOpen()) {
			for (int key : keyList) {
				if (Keyboard.isKeyDown(key)) KeyBinding.setKeyBindState(key, true);
				else KeyBinding.setKeyBindState(key, false);
			}
		} else {
			for (int key : keyList) {
				if (!Keyboard.isKeyDown(key)) KeyBinding.setKeyBindState(key, false);
			}
		}
	}
}
