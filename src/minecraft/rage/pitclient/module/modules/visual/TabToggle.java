package rage.pitclient.module.modules.visual;

import net.minecraft.client.settings.KeyBinding;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.RenderGameOverlayEvent;
import rage.pitclient.events.RenderGameOverlayEvent.ElementType;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;

@ModuleInfo(name = "Tab Toggle", tooltip = "Always displays the player list", category = Category.VISUAL)
public class TabToggle extends Module {
	
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Post event) {
		if (!enabled)
			return;
		if (event.type == ElementType.CHAT)
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindPlayerList.getKeyCode(), true);
		if (event.type == ElementType.ALL)
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindPlayerList.getKeyCode(), false);
	}
}
