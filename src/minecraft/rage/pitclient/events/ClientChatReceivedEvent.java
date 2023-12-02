package rage.pitclient.events;

import net.minecraft.util.IChatComponent;
import rage.pitclient.eventbus.event.Cancelable;

public class ClientChatReceivedEvent extends Cancelable
{
    public IChatComponent message;
    /**
     * Introduced in 1.8:
     * 0 : Standard Text Message
     * 1 : 'System' message, displayed as standard text.
     * 2 : 'Status' message, displayed above action bar, where song notifications are.
     */
    public final byte type;
    public ClientChatReceivedEvent(byte type, IChatComponent message)
    {
        this.type = type;
        this.message = message;
    }
}
