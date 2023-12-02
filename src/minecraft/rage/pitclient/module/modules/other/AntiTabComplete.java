package rage.pitclient.module.modules.other;

import net.minecraft.network.play.client.C14PacketTabComplete;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.packets.PacketEvent;

@ModuleInfo(name = "Anti Tab Complete", tooltip = "Don't send tab packets to server that start with \".\"", category = Category.OTHER)
public class AntiTabComplete extends Module {

	
	@SubscribeEvent
	public void onMessage(PacketEvent.Outgoing.Pre event) {
		if (!enabled)
			return;
		if (event.getPacket() instanceof C14PacketTabComplete) {
			C14PacketTabComplete packet = (C14PacketTabComplete) event.getPacket();
			if (packet.getMessage().startsWith(".")) event.setCanceled(true);
		}
	}
}
