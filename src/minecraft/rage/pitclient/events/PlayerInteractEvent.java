package rage.pitclient.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import rage.pitclient.eventbus.event.Cancelable;

public class PlayerInteractEvent extends Cancelable {

    public static enum Action
    {
        RIGHT_CLICK_AIR,
        RIGHT_CLICK_BLOCK,
        LEFT_CLICK_BLOCK
    }

    public final EntityPlayer entityPlayer;
    public final Action action;
    public final World world;
    public final BlockPos pos;
    public final EnumFacing face; // Can be null if unknown
    public final Vec3 localPos; // Can be null if unknown

    public PlayerInteractEvent(EntityPlayer player, Action action, BlockPos pos, EnumFacing face, World world, Vec3 localPos)
    {
    	this.entityPlayer = player;
        this.action = action;
        this.pos = pos;
        this.face = face;
        this.world = world;
        this.localPos = localPos;
    }
}
