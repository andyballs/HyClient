package rage.pitclient.events;

import rage.pitclient.eventbus.event.Event;
import rage.pitclient.settings.Setting;

public class ClickGuiSettingEvent extends Event {

	public Setting setting;
	
	public ClickGuiSettingEvent(Setting setting) {
		this.setting = setting;
	}
	
}
