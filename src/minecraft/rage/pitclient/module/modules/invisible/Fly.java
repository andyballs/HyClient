package rage.pitclient.module.modules.invisible;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;

@ModuleInfo(name = "Fly Test", category = Category.INVISIBLE)
public class Fly extends Module {

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (!enabled)
			return;
//		if (mc.thePlayer.fallDistance <= 3.5) {
//			PitClient.commandManager.sendModuleMessage(this, EnumChatFormatting.RED + "You must be falling to enable");
//			PitClient.moduleManager.swap(this);
//			return;
//		}
		
		mc.thePlayer.moveFlying(0f, 1f, 1f);
		//mc.thePlayer.motionY = 0;
		
		PlayerCapabilities capabilities = new PlayerCapabilities();
		capabilities.allowFlying = true;
		capabilities.isFlying = true;
		capabilities.setFlySpeed((float)ThreadLocalRandom.current().nextDouble(0.1, 9.0));
		mc.getNetHandler().addToSendQueue(new C13PacketPlayerAbilities(capabilities));
	}
	
	@Override
	public void onDisable() {
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionZ = 0;
	}
	
}
