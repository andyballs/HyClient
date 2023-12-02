package rage.pitclient.events;

import rage.pitclient.eventbus.event.Event;

public abstract class MotionEvent extends Event {
	
    public boolean onGround;
    public double x, y, z;
    public float yaw, pitch;
  
	public MotionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
	}
	
	public static class Pre extends MotionEvent {
		public Pre(double x, double y, double z, float yaw, float pitch, boolean onGround) { super(x, y, z, yaw, pitch, onGround); }
	}
	
	public static class Post extends MotionEvent {
		public Post(double x, double y, double z, float yaw, float pitch, boolean onGround) { super(x, y, z, yaw, pitch, onGround); }
	}
}
