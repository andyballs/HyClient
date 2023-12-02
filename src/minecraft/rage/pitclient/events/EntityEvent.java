package rage.pitclient.events;

import net.minecraft.entity.Entity;
import rage.pitclient.eventbus.event.Cancelable;

/**
 * EntityEvent is fired when an event involving any Entity occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #entity} contains the entity that caused this event to occur.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
public class EntityEvent extends Cancelable
{
    public final Entity entity;

    public EntityEvent(Entity entity)
    {
        this.entity = entity;
    }
}
