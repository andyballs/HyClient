package rage.pitclient.module.modules.movement;

import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.events.TickEvent.Phase;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;

@ModuleInfo(name = "Fast Place", category = Category.MOVEMENT)
public class FastPlace extends Module {

	@SubscribeEvent
	public void onUpdate(ClientTickEvent event) {
		if (!enabled)
			return;
		if (event.phase != Phase.END)
			return;
		mc.rightClickDelayTimer = 0;
	}
}
