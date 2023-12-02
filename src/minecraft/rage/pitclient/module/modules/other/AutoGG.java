package rage.pitclient.module.modules.other;

import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.ClientChatReceivedEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Auto GG", category = Category.OTHER)
public class AutoGG extends Module {

	private Setting delaySet = regSliderTip("Delay", 1000, 0, 5000, true, "Delay in ms to wait before sending 'gg'");
	
	private static final String[] triggers = 
	{"1st Killer - ",
	"1st Place -" ,
	"Winner: ",
	" - Damage Dealt -",
	"Winning Team -",
	"1st - ",
	"Winners:",
	"Winner:" ,
	"Winning Team:",
	" won the game!",
	"Top Seeker:",
	"1st Place:",
	"Last team standing!",
	"Winner #1 (",
	"Top Survivors",
	"Winners - ",
	"Sumo Duel -"};

	@SubscribeEvent
	public void onMessage(ClientChatReceivedEvent event) {
		if (!enabled)
			return;
		String message = event.message.getUnformattedText();
		
		boolean flag = false;
		for (String s : triggers) {
			if (message.contains(s)) flag = true;
		}
		if (!flag) return;
		double delay = delaySet.getValDouble();
		send((long) delay);
	}
	
	private void send(long wait) {
		new Thread(() -> {
			try {
				Thread.sleep(wait);
				mc.thePlayer.sendChatMessage("/achat gg");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}
