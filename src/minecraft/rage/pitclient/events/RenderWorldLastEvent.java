package rage.pitclient.events;

import net.minecraft.client.renderer.RenderGlobal;
import rage.pitclient.eventbus.event.Event;

public class RenderWorldLastEvent extends Event
{
    public final RenderGlobal context;
    public final float partialTicks;
    public RenderWorldLastEvent(RenderGlobal context, float partialTicks)
    {
        this.context = context;
        this.partialTicks = partialTicks;
    }
}