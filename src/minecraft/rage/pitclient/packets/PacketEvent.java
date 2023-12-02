package rage.pitclient.packets;

import net.minecraft.network.Packet;
import rage.pitclient.eventbus.event.Cancelable;

public class PacketEvent extends Cancelable {

	  private Packet<?> packet;
	
	  public PacketEvent(Packet<?> packet) {
		    this.packet = packet;
	  }
	  
	  public Packet<?> getPacket() {
		    return packet;
	  }
	  
	  public void setPacket(Packet<?> packet) {
		  this.packet = packet;
	  }
	  
	  public static class Outgoing extends PacketEvent {
		    
		    public Outgoing(Packet<?> packetIn) {
		    	super(packetIn);
		    }
		    
		    public static class Pre extends Outgoing
		    {
		        public Pre(Packet<?> packetIn)
		        {
		            super(packetIn);
		        }
		    }

		    public static class Post extends Outgoing
		    {
		        public Post(Packet<?> packetIn)
		        {
		            super(packetIn);
		        }
		        @Override public boolean isCancelable(){ return false; }
		    }
		    
	  }
	
	  public static class Incoming extends PacketEvent {
		  
		  public Incoming(Packet<?> packetIn) {
			  super(packetIn);
		  }
		    
		    public static class Pre extends Incoming
		    {
		        public Pre(Packet<?> packetIn)
		        {
		            super(packetIn);
		        }
		    }

		    public static class Post extends Incoming
		    {
		        public Post(Packet<?> packetIn)
		        {
		            super(packetIn);
		        }
		        @Override public boolean isCancelable(){ return false; }
		    }
	  }
	  
}