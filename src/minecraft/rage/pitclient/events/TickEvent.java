package rage.pitclient.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import rage.pitclient.eventbus.event.Event;

public class TickEvent extends Event {
    public enum Type {
        WORLD, PLAYER, CLIENT, RENDER;
    }

    public enum Phase {
        START, END;
    }

	private Type type;
	public Phase phase;
    
    public TickEvent(Type type, Phase phase) {
    	this.type = type;
    	this.phase = phase;
    }
    
    public static class ClientTickEvent extends TickEvent {
        public ClientTickEvent(Phase phase)
        {
            super(Type.CLIENT, phase);
        }
    }
    public static class WorldTickEvent extends TickEvent {
        public final World world;
        public WorldTickEvent(Phase phase, World world)
        {
            super(Type.WORLD, phase);
            this.world = world;
        }
    }
    public static class PlayerTickEvent extends TickEvent {
        public final EntityPlayer player;

        public PlayerTickEvent(Phase phase, EntityPlayer player)
        {
            super(Type.PLAYER, phase);
            this.player = player;
        }
    }

    public static class RenderTickEvent extends TickEvent {
        public final float renderTickTime;
        public RenderTickEvent(Phase phase, float renderTickTime)
        {
            super(Type.RENDER, phase);
            this.renderTickTime = renderTickTime;
        }
    }
}
