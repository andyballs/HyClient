package rage.pitclient.module.modules.other;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.RenderGameOverlayEvent;
import rage.pitclient.events.SoundEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.TextRenderUtils;

@ModuleInfo(name = "Auto Log Glitch", tooltip = "Runs \\\"/l\\\" when the player hits a bow shot", category = Category.OTHER)
public class AutoLog extends Module {

	private Setting onlyWhenLow = regBooleanTip("Only when low", false, "If enabled, Auto Log Glitch will enable when below selected health and won't be toggleable normally");
	private Setting enableHealth = regSlider("Health to enable at", 2.0, 1.0, 20, true);
	
	@SubscribeEvent
	public void onBowSound(SoundEvent.SoundSourceEvent event) {
		if (!enabled)
			return;	
		if (mc.theWorld == null)
			return;
		
		if (event.name.equals("random.successful_hit")) {
			mc.thePlayer.sendChatMessage("/l");
			toggle();
		}
	}
	
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Text event) {
		if (!enabled)
			return;
		if (mc.theWorld == null)
			return;
		
		ScaledResolution sr = new ScaledResolution(mc);
		FontRenderer fr = mc.fontRendererObj;
		
		TextRenderUtils.drawStringCentered(EnumChatFormatting.GOLD + "Hit a player with a bow to log safely.", fr, sr.getScaledWidth()/2, sr.getScaledHeight()/2 - 30, true, 0);
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (mc.theWorld == null)
			return;
		if (!onlyWhenLow.getValBoolean())
			return;
		
		if (mc.thePlayer.getHealth() <= enableHealth.getValDouble()) {
			if (!enabled) {
				debug("ENABLED");
				PitClient.moduleManager.setEnabled(this);
			}
		} else if (enabled) {
			debug("DISABLED");
			PitClient.moduleManager.setDisabled(this);
		}	
	}

}
