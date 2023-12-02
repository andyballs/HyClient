package rage.pitclient.module.modules.combat;

import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Timer", category = Category.COMBAT)
public class Timer extends Module {
	
	private Setting timer = regSlider("Timer", 1.0, 0.5, 2.0, false);
	
	@Override
	public void onDisable() {
		setTimer(1.0F);
	}
	
	@Override
	public void onEnable() {
		double val = timer.getValDouble();
		setTimer((float) val);
	}
	
	private void setTimer(float val) {		
		mc.timer.timerSpeed = val;
	}
	
	private int ticks;
	private double lastTimer;
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (!enabled)
			return;
		if (mc.theWorld == null)
			return;
		ticks++;
		if (ticks % 10 == 0) {
			double val = timer.getValDouble();
			if (val != lastTimer) {
				setTimer((float) val);
			}
		}
	}

}
