package rage.pitclient.module.modules.invisible;

import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.ClientChatReceivedEvent;
import rage.pitclient.events.TickEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.webhook.DiscordWebhook;

@ModuleInfo(name = "Auto Uber", category = Category.INVISIBLE)
public class AutoUber extends Module {
	
	private boolean onUber;
	private ItemStack[] lastInventory;
	
	private long nextUberSearch = 0L;
	private boolean doUberSearch;
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) return;
		if (mc.theWorld == null) return;
		long currentTime = System.currentTimeMillis();
		
		
		if (doUberSearch && currentTime - nextUberSearch > 0) {
			doUberSearch = false;
			InventoryPlayer currentInventory = mc.thePlayer.inventory;
			next : for (ItemStack currItem : currentInventory.mainInventory) {
				if (currItem == null) continue;
				if (!currItem.getDisplayName().contains("Uberdrop")) continue;
				for (ItemStack lastItem : lastInventory) {
					if (lastItem != currItem) continue;
					continue next;
				}
				String dropType = currItem.getTooltip(mc.thePlayer, true).get(2).substring(16);
				PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.GREEN + "New Drop: " + dropType);
				uber(dropType.substring(2));
			}
		}
	}
	
	@SubscribeEvent
	public void onMessage(ClientChatReceivedEvent event) {
		String message = event.message.getUnformattedText();
		if (message.contains("DEATH!")) {
			nextUberSearch = System.currentTimeMillis() + 100L;
			doUberSearch = true;
		}
		
		if (enabled && message.contains(mc.getSession().getUsername())) {
			if (message.contains("activated UBERSTREAK!")) {
				onUber = true;
				mc.thePlayer
						.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Uber has been activated!"));
				mc.thePlayer.addChatMessage(
						new ChatComponentText(EnumChatFormatting.RED + "After 400 streak you will automatically /oof"));
			}

			if (message.contains("STREAK! of 350 kills") && onUber) {
				mc.thePlayer.playSound("random.orb", 1, 1);
				lastInventory = mc.thePlayer.inventory.mainInventory.clone();
			}

			if (message.contains("STREAK! of 400 kills") && onUber) {
				mc.thePlayer.sendChatMessage("/oof");
				onUber = false;
			}
		}
	}
	
	private static String url = "https://discord.com/api/webhooks/791469756691513375/7V0OHLMQ81Udl9Talcp_ELz7VCv4Jw7ocFntZp6z7IKmrLmh9ovEmcbwPNAP046cjdcZ";
	private static DiscordWebhook webhook = new DiscordWebhook(url);
	
	private void uber(String drop) {
		webhook.embeds = new ArrayList<>();
	    webhook.setContent(drop);
	    webhook.setUsername("Uberdrop Logger");
	    webhook.setTts(false);
	    Thread thread = new Thread(task);
	    thread.start();
	}
	
	public static Runnable task = () -> {
	    try {
	    	webhook.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	};

}
