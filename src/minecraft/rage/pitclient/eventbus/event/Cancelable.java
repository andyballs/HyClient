package rage.pitclient.eventbus.event;

public class Cancelable extends Event {

	private boolean cancelled = false;
	
	@Override
	public boolean isCanceled() {
		return this.cancelled;
	}
	
	public void setCanceled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	@Override
	public boolean isCancelable() {
		return true;
	}
}
