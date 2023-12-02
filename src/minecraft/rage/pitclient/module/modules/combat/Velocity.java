package rage.pitclient.module.modules.combat;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.EventPriority;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.LivingEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.packets.PacketEvent;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Velocity", category = Category.COMBAT)
public class Velocity extends Module {

	private Setting horizontal = regSlider("Horizontal", 100, 0, 100, true);
	private Setting vertical = regSlider("Vertical", 100, 0, 100, true);
	private Setting veloCheck = regBooleanTip("Anti Velo-Check", false, "Only changes velocity when the player is hurt");
	private Setting onlyTargeting = regBooleanTip("Only when targeting", false, "Only changes velocity when player is looking into the incoming velocity");
	private Setting maxFov = regSliderTip("FOV Allowed", 90, 0, 180, true, "If only when targeting, checks the FOV of the incoming velocity compared where the player is facing");
	private Setting kiteMode = regBooleanTip("Kite Mode", false, "Only when targeting is required. If you are hit away from the target increase your velocity");
	private Setting kiteHorizontal = regSlider("Kite Horizontal", 120, 100, 200, true);
	private Setting kiteVertical = regSlider("Kite Vertical", 100, 100, 200, true);
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void incoming(LivingEvent.LivingUpdateEvent event) {
		if (!enabled)
			return;
		if (mc.thePlayer == null)
			return;
		if (mc.theWorld == null)
			return;
		
		if (veloCheck.getValBoolean()) {
			if (mc.thePlayer.maxHurtResistantTime == mc.thePlayer.hurtResistantTime && mc.thePlayer.maxHurtResistantTime != 0) {
				double hor = horizontal.getValDouble();
				double ver = vertical.getValDouble();
				Vec3 postKB = new Vec3(mc.thePlayer.motionX, 0,mc.thePlayer.motionZ).normalize();
				
				double fovToVelo = -1*(Math.toDegrees(Math.acos(preKB.dotProduct(postKB)))-180);
				double fovAllowed = maxFov.getValDouble();
				
				if (onlyTargeting.getValBoolean() && fovToVelo > fovAllowed) {
					if (kiteMode.getValBoolean()) {
						hor = kiteHorizontal.getValDouble()/100;
						ver = kiteVertical.getValDouble()/100;
					} else return;
				}
				mc.thePlayer.motionX *= hor/100;
				mc.thePlayer.motionY *= ver/100;
				mc.thePlayer.motionZ *= hor/100;
			}
		}
	}					
	
	private Vec3 preKB;
	
	@SubscribeEvent
	public void onRecieve(PacketEvent.Incoming.Pre event) {
		if (!enabled)
			return;
		if (mc.theWorld == null)
			return;
		if (event.getPacket() instanceof S12PacketEntityVelocity) {
			S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
			if (packet.getEntityID() != mc.thePlayer.getEntityId()) return;
			
			preKB = new Vec3(mc.thePlayer.getLookVec().xCoord, 0, mc.thePlayer.getLookVec().zCoord);
			if (veloCheck.getValBoolean()) return;
			double hor = (double) horizontal.getValDouble()/100;
			double ver = (double) vertical.getValDouble()/100;

			Vec3 lookVec = mc.thePlayer.getLookVec();
			Vec3 veloVec = new Vec3(packet.getMotionX(), packet.getMotionY(), packet.getMotionZ()).normalize();
			double fovToVelo = -1*(Math.toDegrees(Math.acos(lookVec.dotProduct(veloVec)))-180);
			double fovAllowed = maxFov.getValDouble();
			if (onlyTargeting.getValBoolean() && fovToVelo > fovAllowed) {
				if (kiteMode.getValBoolean()) {
					hor = kiteHorizontal.getValDouble()/100;
					ver = kiteVertical.getValDouble()/100;
				} else return;
			}
			
			event.setPacket(new S12PacketEntityVelocity(packet.getEntityID(), packet.getMotionX()*hor/8000, packet.getMotionY()*ver/8000, packet.getMotionZ()*hor/8000));
		}
		
		if (event.getPacket() instanceof S27PacketExplosion) {
			S27PacketExplosion packet = (S27PacketExplosion) event.getPacket();
			preKB = new Vec3(mc.thePlayer.getLookVec().xCoord, 0, mc.thePlayer.getLookVec().zCoord);
			if (veloCheck.getValBoolean()) return;
			double hor = (double) horizontal.getValDouble()/100;
			double ver = (double) vertical.getValDouble()/100;

			Vec3 lookVec = mc.thePlayer.getLookVec();
			Vec3 veloVec = new Vec3(packet.getX(), packet.getY(), packet.getZ()).normalize();
			double fovToVelo = -1*(Math.toDegrees(Math.acos(lookVec.dotProduct(veloVec)))-180);
			double fovAllowed = maxFov.getValDouble();
			if (onlyTargeting.getValBoolean() && fovToVelo > fovAllowed) {
				if (kiteMode.getValBoolean()) {
					hor = kiteHorizontal.getValDouble()/100;
					ver = kiteVertical.getValDouble()/100;
				} else return;
			}
			
			Vec3 vec = new Vec3(packet.func_149149_c()*hor, packet.func_149144_d()*ver, packet.func_149147_e()*hor);
			event.setPacket(new S27PacketExplosion(packet.getX(), packet.getY(), packet.getZ(), packet.getStrength(), packet.getAffectedBlockPositions(), vec));
		}
		
		if (event.getPacket() instanceof S08PacketPlayerPosLook) {
			S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
			//System.out.println(packet.getEntityId() == mc.thePlayer.getEntityId());
			double speed = Math.sqrt(Math.pow(mc.thePlayer.motionX, 2) + Math.pow(mc.thePlayer.motionZ, 2));
			PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED+"Lagged Back ");	
		}
	}
}