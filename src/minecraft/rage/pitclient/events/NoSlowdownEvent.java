package rage.pitclient.events;

import rage.pitclient.eventbus.event.Event;

public class NoSlowdownEvent extends Event {

	public float strafe = 0.2F;
	public float forward = 0.2F;
	
	public NoSlowdownEvent() {
		
	}
}
