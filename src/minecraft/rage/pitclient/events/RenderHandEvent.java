package rage.pitclient.events;

import net.minecraft.client.renderer.RenderGlobal;
import rage.pitclient.eventbus.event.Cancelable;

public class RenderHandEvent extends Cancelable
{
    public final RenderGlobal context;
    public final float partialTicks;
    public final int renderPass;
    public RenderHandEvent(RenderGlobal context, float partialTicks, int renderPass)
    {
        this.context = context;
        this.partialTicks = partialTicks;
        this.renderPass = renderPass;
    }
}
