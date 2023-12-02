package rage.pitclient.module.modules.other;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.events.TickEvent.Phase;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;

@ModuleInfo(name = "Vanish Detector", tooltip = "Doesn't matter how it works, keep it on", category = Category.OTHER, permission = 1, defaultEnabled = true)
public class VanishDetector extends Module {

	private int ticks;
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (!enabled)
			return;
		if (event.phase != Phase.END)
			return;
		if (mc.theWorld == null)
			return;
		if (ticks++ % 20 == 0) {
			
			PitClient.scoreboardManager.vanishedTeams.forEach(team -> {
				String name = (String) team.getMembershipCollection().toArray()[0];
				String msg = ScorePlayerTeam.formatPlayerName(team, name);
				if (!PitClient.getClientUser().beta) msg = EnumChatFormatting.DARK_GREEN + name;
				
				PitClient.commandManager.sendModuleMessage(this, msg);
			});
			
		}
	}
}
