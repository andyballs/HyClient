package rage.pitclient;

import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.config.Config;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.packets.PacketEvent;

public class FriendEnemyManager {

	private Config config;
	
	private ArrayList<String> playerList = new ArrayList<>();
	private ArrayList<String> friendList = new ArrayList<>();
	private ArrayList<String> enemyList = new ArrayList<>();
	
	public FriendEnemyManager() {
		load();
	}
	
	public void save() {
		for (String playerName : playerList) {
			JsonObject object = new JsonObject();
			object.addProperty("friend", Boolean.valueOf(friendList.contains(playerName)));
			config.set(playerName, object);
		}
		config.save();
	}
	
	public void load() {
		File configFile = new File(PitClient.getInstance().pitModConfigManager.configDir, "friends_enemies.json");
		
		(config = new Config(configFile)).load(false);
		if (config.getObject() == null) return;
		
		for (Entry<String, JsonElement> entry : config.getObject().entrySet()) {
			String playerName = entry.getKey();
			
			JsonObject value = entry.getValue().getAsJsonObject();
			boolean friend = value.get("friend").getAsBoolean();
			
			playerList.add(playerName);
			if (friend) friendList.add(playerName);
			if (!friend) enemyList.add(playerName);
		}
	}
	
	public boolean addOrRemoveFriend(String name) {
		if (isFriend(name)) {
			friendList.removeIf(n -> n.equalsIgnoreCase(name));
			return false;
		} else {
			if (isEnemy(name)) enemyList.removeIf(n -> n.equalsIgnoreCase(name));
			friendList.add(name);
			return true;
		}
	}
	
	public boolean isFriend(String name) {
		for (String n : friendList) {
			if (n.equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public boolean isEnemy(String name) {
		for (String n : enemyList) {
			if (n.equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	@SubscribeEvent
	public void onMessage(PacketEvent.Outgoing.Pre event) {		
		if (event.getPacket() instanceof C01PacketChatMessage) {
			C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
			String message = packet.getMessage();
			if (!message.startsWith(".f") && !message.startsWith(".e")) return;
			event.setCanceled(true);
			String[] split = message.split(" ");
			if (split.length <= 1) return;
			if (split[1].equalsIgnoreCase("clear")) {
				if (split[2].equalsIgnoreCase(".e")) enemyList.clear();
				else friendList.clear();
				save();
				return;
			}
			if (split.length != 3) return;
			if (!split[1].equalsIgnoreCase("add") && !split[1].equalsIgnoreCase("remove")) return;
			String name = split[2];
			String msg = split[1].equalsIgnoreCase("add") ? "added" : "removed";
			playerList.add(name);
			if (split[0].startsWith(".f")) {
				if (split[1].equalsIgnoreCase("add") && !friendList.contains(name)) friendList.add(name);
				else friendList.removeIf(n -> n.equalsIgnoreCase(name));
				PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.GREEN + name + EnumChatFormatting.AQUA + " has been "+ msg +" to your friend list.");
			}
			if (split[0].startsWith(".e")) {
				if (split[1].equalsIgnoreCase("add") && !enemyList.contains(name)) enemyList.add(name);
				else enemyList.removeIf(n -> n.equalsIgnoreCase(name));
				PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + name + EnumChatFormatting.AQUA + " has been "+ msg +" to your enemy list.");
			}
			save();
		}
	}
}
