package rage.pitclient.util;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import rage.pitclient.PitClient;

public class ReachUtils {
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
    public static float[] getRotations(EntityLivingBase entityIn, float speed) {
        float yaw = updateRotation(mc.thePlayer.rotationYaw,
                getNeededRotations(entityIn)[0],
                speed);
        float pitch = updateRotation(mc.thePlayer.rotationPitch,
                getNeededRotations(entityIn)[1],
                speed);
        return new float[]{yaw, pitch};
    }
    
    private static float updateRotation(float currentRotation, float intendedRotation, float increment) {
        float f = MathHelper.wrapAngleTo180_float(intendedRotation - currentRotation);

        if (f > increment)
            f = increment;

        if (f < -increment)
            f = -increment;

        return currentRotation + f;
    }
    
    public static float[] getNeededRotations(EntityLivingBase entityIn) {
        double d0 = entityIn.posX - mc.thePlayer.posX;
        double d1 = entityIn.posZ - mc.thePlayer.posZ;
        double d2 = entityIn.posY + entityIn.getEyeHeight() - (mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight());

        double d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        float f = (float) (MathHelper.atan2(d1, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(d2, d3) * 180.0D / Math.PI));
        return new float[]{f, f1};
    }

	public static MovingObjectPosition getMouseOver(double distance) {
	    return getMouseOver(distance, 0.0D);
	}
	
	public static double distanceToEntity;
	
	public static MovingObjectPosition getMouseOver(double distance, double expand) {
	      if (Minecraft.getMinecraft().getRenderViewEntity() != null && Minecraft.getMinecraft().theWorld != null) {
	         Entity entity = null;
	         Vec3 var6 = Minecraft.getMinecraft().getRenderViewEntity().getPositionEyes(1.0F);
	         Vec3 var7 = Minecraft.getMinecraft().getRenderViewEntity().getLook(1.0F);
	         Vec3 var8 = var6.addVector(var7.xCoord * distance, var7.yCoord * distance, var7.zCoord * distance);
	         Vec3 var9 = null;
	         
	         List<Entity> var11 = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABBExcludingEntity(Minecraft.getMinecraft().getRenderViewEntity(), Minecraft.getMinecraft().getRenderViewEntity().getEntityBoundingBox().addCoord(var7.xCoord * distance, var7.yCoord * distance, var7.zCoord * distance).expand(1.0, 1.0, 1.0));
	         double var12 = distance;
	         int var13 = 0;
	         while(true) {
	            if (var13 >= var11.size()) {
	               if (var12 <= distance && (entity instanceof EntityLivingBase || entity instanceof EntityItemFrame)) {
	            	   distanceToEntity = var12 + expand;
	            	   return new MovingObjectPosition(entity, var9);
	               }
	               break;
	            }

	            Entity var14 = (Entity) var11.get(var13);
	            if (!PitClient.friendManager.isFriend(EnumChatFormatting.getTextWithoutFormattingCodes(var14.getName()))) {
		            if (var14.canBeCollidedWith()) {
			               float var15 = var14.getCollisionBorderSize();
			               AxisAlignedBB var16 = var14.getEntityBoundingBox().expand((double)var15, (double)var15, (double)var15);
			               var16 = var16.expand(expand, expand, expand);
			               MovingObjectPosition var17 = var16.calculateIntercept(var6, var8);
			               if (var16.isVecInside(var6)) {
			                  if (0.0D < var12 || var12 == 0.0D) {
			                     entity = var14;
			                     var9 = var17 == null ? var6 : var17.hitVec;
			                     var12 = 0.0D;
			                  }
			               } else if (var17 != null) {
			                  double var18 = var6.distanceTo(var17.hitVec);
			                  if (var18 < var12 || var12 == 0.0D) {
			                     if (var14 == Minecraft.getMinecraft().getRenderViewEntity().ridingEntity && (entity == null)) {
			                        if (var12 == 0.0D) {
			                           entity = var14;
			                           var9 = var17.hitVec;
			                        }
			                     } else {
			                        entity = var14;
			                        var9 = var17.hitVec;
			                        var12 = var18;
			                     }
			                  }
			               }
			            }
	            }
	            ++var13;
	         }
	      }

	      return null;
	   }
	
}
