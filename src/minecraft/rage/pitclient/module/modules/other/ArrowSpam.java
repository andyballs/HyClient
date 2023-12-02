package rage.pitclient.module.modules.other;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBow;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;

@ModuleInfo(name = "Arrow Spammer", tooltip = "Enable to spam arrows", category = Category.OTHER)
public class ArrowSpam extends Module {
	
	private int ticks;
	private BowPhase currentPhase;
	private int nextPacket;
	
	@Override
	public void onEnable() {
		if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) {
			currentPhase = BowPhase.Holding;
			nextPacket = ticks + 2;
			KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
		}
	}
	
	@Override
	public void onDisable() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
		if (currentPhase == BowPhase.Release) mc.playerController.onStoppedUsingItem(mc.thePlayer);
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (!enabled)
			return;
		if (event.phase != TickEvent.Phase.END) 
			return;
		if (mc.thePlayer == null) 
			return;
		ticks++;
		
		if (ticks == nextPacket & enabled) {
			if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) {
					nextPacket = ticks + 2;
					switch (currentPhase) {
					case Holding:
						currentPhase = BowPhase.Release;
						mc.playerController.onStoppedUsingItem(mc.thePlayer);
						break;
					case Release:
						currentPhase = BowPhase.Holding;
						mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
						break;
					}
			}
		}
	}
	
	enum BowPhase {
		Holding,
		Release;
	}
}
