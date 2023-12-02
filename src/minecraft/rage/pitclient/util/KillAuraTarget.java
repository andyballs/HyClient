package rage.pitclient.util;

import net.minecraft.entity.player.EntityPlayer;

public class KillAuraTarget {
	
	public EntityPlayer entity;
	public double fov;
	
	public KillAuraTarget(EntityPlayer entity, double fov) {
		this.entity = entity;
		this.fov = fov;
	}
}
