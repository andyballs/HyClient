package rage.pitclient.module.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S3EPacketTeams;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent;
import rage.pitclient.events.TickEvent.Phase;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.packets.PacketEvent;
import rage.pitclient.packets.WrappedS3EPacketTeams;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Anti Bot", defaultEnabled = true, tooltip = "Removes WD Bot from the world as it joins", category = Category.COMBAT)
public class AntiBot extends Module {
	
	private Setting name = regBooleanTip("Bot Name in Chat", true, "Sends WD Bot's name in chat as it gets detected");
	
	private String currentBotName;
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase != Phase.END)
			return;
		if (mc.theWorld == null) 
			return;
		if (currentBotName == null) 
			return;
		
		Entity botByName = mc.theWorld.getPlayerEntityByName(currentBotName);
		if (botByName != null) {
			mc.theWorld.removeEntityFromWorld(botByName.getEntityId());
			if (name.getValBoolean()) PitClient.commandManager.sendModuleMessage(this, botByName.getDisplayName().getFormattedText());
		}
	}
	
	@SubscribeEvent
	public void onRecieve(PacketEvent.Incoming.Pre event) {
		if (mc.theWorld == null)
			return;
		
		if (event.getPacket() instanceof S3EPacketTeams) {
			WrappedS3EPacketTeams packet = new WrappedS3EPacketTeams((S3EPacketTeams) event.getPacket());
			
			if (packet.getColorIndex() == 12) {
				currentBotName = packet.getFirstPlayer();
			}
		}
	}	
}
