package rage.pitclient.module.modules.movement;

import net.minecraft.client.settings.KeyBinding;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;

@ModuleInfo(name = "Sprint", tooltip = "Player will always sprint", category = Category.MOVEMENT, defaultEnabled = true)
public class Sprint extends Module {

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (!enabled)
			return;
		if (mc.theWorld == null)
			return;
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
	}
}
