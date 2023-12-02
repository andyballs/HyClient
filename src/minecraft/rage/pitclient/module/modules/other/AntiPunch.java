package rage.pitclient.module.modules.other;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.ClientChatReceivedEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.packets.PacketEvent;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Anti Punch", tooltip = "Stops vertical movement after punch", category = Category.OTHER)
public class AntiPunch extends Module {

	private Setting punchTicks = regSliderTip("Ticks", 4, 2, 8, true, "How many ticks to cancel velocity for");

	private boolean veloFlag;
	private double ticks;
	private double lastTick;
	
	@SubscribeEvent
	public void onPunchMessage(ClientChatReceivedEvent event) {
		String message = event.message.getUnformattedText();
		if (message.contains("PUNCH!") && message.contains(mc.thePlayer.getName() + " by ")) {
			lastTick = ticks + punchTicks.getValDouble();
		}
	}
	
	@SubscribeEvent
	public void onUpdate(ClientTickEvent event) {
		if (!enabled)
			return;
		if (mc.theWorld == null)
			return;
		ticks++;
		
		if (veloFlag) {
			veloFlag = false;
			mc.thePlayer.setVelocity(mc.thePlayer.motionX, 0, mc.thePlayer.motionZ);
		}
	}
	
	@SubscribeEvent
	public void incoming(PacketEvent.Incoming.Pre event) {
		if (ticks > lastTick)
			return;
		if (!enabled)
			return;
		if (mc.theWorld == null)
			return;
		if (event.getPacket() instanceof S12PacketEntityVelocity) {
			veloFlag = true;
			event.setCanceled(true);
		}
		if (event.getPacket() instanceof S27PacketExplosion) {
			veloFlag = true;
			event.setCanceled(true);
		}
	}
}
