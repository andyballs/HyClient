package rage.pitclient.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.AttackEntityEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Keep Sprint", tooltip = "Keeps percentage of sprint after attacking", category = Category.COMBAT)
public class KeepSprint extends Module {
	
	private Setting keepPercent = regSlider("Percent Kept", 100.0, 60.0, 120.0, true);
	private Setting maxSpeed = regSliderTip("Max Speed", 0.34, 0.3, 0.4, false, "If horizontal speed goes above this, 100% sprint is kept");

	@SubscribeEvent
	public void onAttack(AttackEntityEvent event) {
		if (!enabled)
			return;
		if (!mc.thePlayer.isSprinting())
			return;
		event.setCanceled(true);
		Entity targetEntity = event.target;
		EntityPlayer thePlayer = Minecraft.getMinecraft().thePlayer;
		if (targetEntity.canAttackWithItem())
        {
            if (!targetEntity.hitByEntity(thePlayer))
            {
                float f = (float)thePlayer.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                int i = 0;
                float f1 = 0.0F;

                if (targetEntity instanceof EntityLivingBase)
                {
                    f1 = EnchantmentHelper.func_152377_a(thePlayer.getHeldItem(), ((EntityLivingBase)targetEntity).getCreatureAttribute());
                }
                else
                {
                    f1 = EnchantmentHelper.func_152377_a(thePlayer.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
                }

                i = i + EnchantmentHelper.getKnockbackModifier(thePlayer);

                if (thePlayer.isSprinting())
                {
                    ++i;
                }

                if (f > 0.0F || f1 > 0.0F)
                {
                    boolean flag = thePlayer.fallDistance > 0.0F && !thePlayer.onGround && !thePlayer.isOnLadder() && !thePlayer.isInWater() && !thePlayer.isPotionActive(Potion.blindness) && thePlayer == null && targetEntity instanceof EntityLivingBase;

                    if (flag && f > 0.0F)
                    {
                        f *= 1.5F;
                    }

                    f = f + f1;
                    boolean flag1 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(thePlayer);

                    if (targetEntity instanceof EntityLivingBase && j > 0 && !targetEntity.isBurning())
                    {
                        flag1 = true;
                        targetEntity.setFire(1);
                    }

                    double d0 = targetEntity.motionX;
                    double d1 = targetEntity.motionY;
                    double d2 = targetEntity.motionZ;
                    boolean flag2 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(thePlayer), f);

                    if (flag2)
                    {
                        if (i > 0)
                        {
                            targetEntity.addVelocity((double)(-MathHelper.sin(thePlayer.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(thePlayer.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                            
                            double percent = keepPercent.getValDouble();
                            double mult = percent/100;
                            double max = maxSpeed.getValDouble();
                            double nextSpeed = Math.sqrt(Math.pow(thePlayer.motionX*mult, 2) + Math.pow(thePlayer.motionZ*mult, 2));
                            if (nextSpeed <= max) {
                            	//PitMod.commandManager.sendModuleMessage(this, String.valueOf(playerSpeed));
                            	thePlayer.motionX *= mult;
                                thePlayer.motionZ *= mult;
                            } else {
                            	double currentSpeed = Math.sqrt(Math.pow(thePlayer.motionX, 2) + Math.pow(thePlayer.motionZ, 2));
                            	//PitMod.commandManager.sendModuleMessage(this, String.valueOf(currentSpeed) + " MAXED");
                            }
                            //thePlayer.setSprinting(false);
                        }

                        if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged)
                        {
                            ((EntityPlayerMP)targetEntity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = d0;
                            targetEntity.motionY = d1;
                            targetEntity.motionZ = d2;
                        }

                        if (flag)
                        {
                        	thePlayer.onCriticalHit(targetEntity);
                        }

                        if (f1 > 0.0F)
                        {
                        	thePlayer.onEnchantmentCritical(targetEntity);
                        }

                        if (f >= 18.0F)
                        {
                        	thePlayer.triggerAchievement(AchievementList.overkill);
                        }

                        thePlayer.setLastAttacker(targetEntity);

                        if (targetEntity instanceof EntityLivingBase)
                        {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, thePlayer);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(thePlayer, targetEntity);
                        ItemStack itemstack = thePlayer.getCurrentEquippedItem();
                        Entity entity = targetEntity;

                        if (targetEntity instanceof EntityDragonPart)
                        {
                            IEntityMultiPart ientitymultipart = ((EntityDragonPart)targetEntity).entityDragonObj;

                            if (ientitymultipart instanceof EntityLivingBase)
                            {
                                entity = (EntityLivingBase)ientitymultipart;
                            }
                        }

                        if (itemstack != null && entity instanceof EntityLivingBase)
                        {
                            itemstack.hitEntity((EntityLivingBase)entity, thePlayer);

                            if (itemstack.stackSize <= 0)
                            {
                            	thePlayer.destroyCurrentEquippedItem();
                            }
                        }

                        if (targetEntity instanceof EntityLivingBase)
                        {
                        	thePlayer.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));

                            if (j > 0)
                            {
                                targetEntity.setFire(j * 4);
                            }
                        }

                        thePlayer.addExhaustion(0.3F);
                    }
                    else if (flag1)
                    {
                        targetEntity.extinguish();
                    }
                }
            }
        }
	}
}
