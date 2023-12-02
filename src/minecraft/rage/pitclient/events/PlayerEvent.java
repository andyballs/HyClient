package rage.pitclient.events;

import net.minecraft.entity.player.EntityPlayer;
import rage.pitclient.eventbus.event.Cancelable;

public class PlayerEvent extends Cancelable {
	
    public final EntityPlayer entityPlayer;
    public PlayerEvent(EntityPlayer player)
    {
        entityPlayer = player;
    }

}
