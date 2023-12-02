package rage.pitclient.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.SoundEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.events.TickEvent.Phase;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Venom Swapper", tooltip = "When you are venomed, swaps to diamond pants/boots automatically", category = Category.COMBAT)
public class VenomSwapper extends Module {

	private Setting swapBoots = regBooleanTip("Auto Swap Boots", true, "Swaps from Armageddon boots to Diamond boots");
	private Setting swapPants = regBooleanTip("Auto Swap Leggings", true, "Swaps from any leggings to Diamond leggings");

	public static KeyBinding pantsSwap;
	public static KeyBinding bootSwap;
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (event.phase != Phase.END)
			return;
		if (mc.theWorld == null)
			return;
		
		if (pantsSwap.isPressed()) autoSwap(Swap.Pants);
		if (bootSwap.isPressed()) bootsSwap();
	}
	
	@SubscribeEvent
	public void onSound(SoundEvent.SoundSourceEvent event) {
//		if (!PitClient.getInstance().getManager().isOnPit())
//			return;
		if (!enabled)
			return;

		if (event.name.equals("mob.spider.say")) {
			
			if (swapPants.getValBoolean()) autoSwap(Swap.Pants);
			if (swapBoots.getValBoolean()) autoSwap(Swap.Boots);
		}
	}
	
	private void autoSwap(Swap type) {
		String formalName = "item.leggingsDiamond";
		String name = "Diamond Pants";
		int slot = 7;
		
		if (type == Swap.Boots) {
			formalName = "item.bootsDiamond";
			name = "Diamond Boots";
			slot = 8;
		}
		for (Slot item : mc.thePlayer.inventoryContainer.inventorySlots) {
			if (item.getStack() != null && item.getStack().getItem().getUnlocalizedName().equalsIgnoreCase(formalName)) {
				if (item.slotNumber == slot) {
					PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.GREEN + "You're already wearing " + name + "!");
					return;
				}
				
				mc.playerController.windowClick(0, item.slotNumber, 7, 2, Minecraft.getMinecraft().thePlayer);
				mc.playerController.windowClick(0, slot, 7, 2, Minecraft.getMinecraft().thePlayer);
				mc.playerController.windowClick(0, item.slotNumber, 7, 2, Minecraft.getMinecraft().thePlayer);

				PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.GREEN + "Swapped to " + name + "!");
			}
		}
	}
	
	private void bootsSwap() {
		String armName = "item.bootsCloth";
		String bootsName = "item.bootsDiamond";
		
		Slot swapTo = null;
		Slot current = null;
		
		for (Slot item : mc.thePlayer.inventoryContainer.inventorySlots) {
			if (item.getStack() == null) continue;
			if (item.slotNumber == 8) {
				current = item;
				continue;
			}
			if (item.getStack().getItem().getUnlocalizedName().equalsIgnoreCase(armName)) swapTo = item;
			if (item.getStack().getItem().getUnlocalizedName().equalsIgnoreCase(bootsName)) swapTo = item;
		}
			
		if (swapTo == null | current == null) return;
		
		mc.playerController.windowClick(0, swapTo.slotNumber, 7, 2, Minecraft.getMinecraft().thePlayer);
		mc.playerController.windowClick(0, current.slotNumber, 7, 2, Minecraft.getMinecraft().thePlayer);
		mc.playerController.windowClick(0, swapTo.slotNumber, 7, 2, Minecraft.getMinecraft().thePlayer);

		mc.thePlayer.addChatComponentMessage(new ChatComponentText(
				PitClient.commandPrefix + EnumChatFormatting.GREEN + "Swapped Boots!"));
		
	}
	
	private enum Swap {
		Boots,
		Pants;
	}
}
