package rage.pitclient.module.modules.invisible;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.packets.PacketEvent;

@ModuleInfo(name = "Blink", category = Category.INVISIBLE)
public class Blink extends Module {
	@Override
	public void onDisable() {
		stopBlink();
	}
	
	@SubscribeEvent
	public void onRecieve(PacketEvent.Outgoing.Pre event) {
		if (!enabled)
			return;
		if (event.getPacket() instanceof C00PacketKeepAlive)
			return;
		packetList.add(event.getPacket());
		event.setCanceled(true);
	}
	
	private ArrayList<Packet<?>> packetList = new ArrayList<Packet<?>>();
	
	private void stopBlink() {
		packetList.forEach(packet -> {
			Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
		});
		packetList.clear();
	}
	
	
}
