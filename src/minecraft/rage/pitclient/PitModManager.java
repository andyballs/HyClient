package rage.pitclient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import rage.pitclient.util.MiscUtils;

public class PitModManager {
	private long telebowCooldownEnd = 0L;
	private long telebowCooldownEndOld = 0L;
	private long auraProtectionEnd = 0L;
	private TreeSet<Long> telebowArrowFires = new TreeSet<>();
	private Set<String> playersOld = new HashSet<>();

	private float currentPrestigeGold = -1.0F;
	private float currentPrestigeGoldMax = -1.0F;

	public Set<String> tabListCurrent = new HashSet<>();
	public final HashMap<String, String> currentPlayerMap = new HashMap<>();
	public final HashMap<String, String> tracerMap = new HashMap<>();

	public final HashMap<String, String> denicked = new HashMap<>();
	public final HashMap<String, Boolean> nickMap = new HashMap<>();
	private final Set<String> nicks = new HashSet<>();

	private boolean onPit = false;
	public boolean vanishMacro = false;
	
	public HashMap<String, String> darkMap = new HashMap<>();

	public boolean search;
	public int searchAmnt;

	public float getGoldReqForPres(int pres) {
		float m = 1000000.0F;
		float k = 1000.0F;
		switch (pres) {
		case 0:
			return 10.0F * k;
		case 1:
			return 22.0F * k;
		case 2:
			return 24.0F * k;
		case 3:
			return 26.0F * k;
		case 4:
			return 28.0F * k;
		case 5:
			return 30.0F * k;
		case 6:
			return 70.0F * k;
		case 7:
			return 80.0F * k;
		case 8:
			return 100.0F * k;
		case 9:
			return 120.0F * k;
		case 10:
			return 160.0F * k;
		case 11:
			return 200.0F * k;
		case 12:
			return 240.0F * k;
		case 13:
			return 280.0F * k;
		case 14:
			return 320.0F * k;
		case 15:
			return 360.0F * k;
		case 16:
			return 400.0F * k;
		case 17:
			return 480.0F * k;
		case 18:
			return 560.0F * k;
		case 19:
			return 800.0F * k;
		case 20:
			return 900.0F * k;
		case 21:
			return 1000.0F * k;
		case 22:
			return 1200.0F * k;
		case 23:
			return 1400.0F * k;
		case 24:
			return 1.6F * m;
		case 25:
			return 1.8F * m;
		case 26:
			return 2.4F * m;
		case 27:
			return 2.7F * m;
		case 28:
			return 3.0F * m;
		case 29:
			return 6.0F * m;
		case 30:
			return 10.0F * m;
		case 31:
			return 12.12F * m;
		case 32:
			return 14.14F * m;
		case 33:
			return 16.16F * m;
		case 34:
			return 18.18F * m;
		case 35:
			return 0.0F;
		}

		return -1.0F;
	}

	public boolean isPlayerNicked(String playerName) {
		if (nickMap.containsKey(playerName)) {
			return ((Boolean) this.nickMap.get(playerName)).booleanValue();
		}
		HashMap<String, String> args = new HashMap<>();
		args.put("name", playerName);
		PitClient.getInstance().getHypixelApi().getHypixelApiAsync(
				PitClient.getInstance().pitModConfigManager.getConfig().get("apikey").getAsString(), "player", args, json -> {
					if (json.has("success") && json.get("success").getAsBoolean()) {
						nickMap.put(playerName, Boolean.valueOf(json.get("player").isJsonNull()));
						if (json.get("player").isJsonNull() == true)
							denicker(playerName);
					} else {
						System.out.println(playerName + " API error.");
					}
				});

		nickMap.put(playerName, Boolean.valueOf(false));
		return false;
	}

	public void denicker(String playerName) {
		if (!isPlayerNicked(playerName)) {
			sendMessage("Player is not nicked. ", playerName);
			return;
		}
		EntityPlayer nick = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(playerName);
		ItemStack pant = nick.inventory.armorItemInSlot(1);
		if (pant != null) {
			NBTTagCompound tag = pant.getTagCompound();
			if (tag != null) {
				NBTTagCompound extraAttributes = pant.getTagCompound().getCompoundTag("ExtraAttributes");
				if (extraAttributes != null) {
					Integer nonce = extraAttributes.getInteger("Nonce");
					if (nonce != 0 && nonce != null) {
						checkNonce(nonce, playerName);
					}
				}
			}
		}

		ItemStack holding = nick.getHeldItem();
		if (holding != null) {
			NBTTagCompound tag2 = holding.getTagCompound();
			if (tag2 != null) {
				NBTTagCompound extraAttributes2 = tag2.getCompoundTag("ExtraAttributes");
				if (extraAttributes2 != null) {
					Integer nonce2 = extraAttributes2.getInteger("Nonce");
					if (nonce2 != 0 && nonce2 != null) {
						checkNonce(nonce2, playerName);
					}
				}
			}
		}
	}

	private void checkNonce(Integer nonce, String player) {
		PitClient.getInstance().getHypixelApi().getPitPandaAsync(nonce, json -> {
			if (json.has("success") && json.get("success").getAsBoolean()) {
				JsonElement items = json.get("items");
				if (items.getAsJsonArray() != null) {
					System.out.println(items.getAsJsonArray());
					if (!items.getAsJsonArray().toString().equals("[]")) {
						String uuid = items.getAsJsonArray().get(0).getAsJsonObject().get("owner").getAsString();
						String owner = MiscUtils.getNameFromUUID(uuid);
						denicked.put(player, owner);
						
						FontRenderer fr = (Minecraft.getMinecraft()).fontRendererObj;

						StringBuilder alertMsg = new StringBuilder(EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD
								+ "Denicked: " + player + " is " + owner);

						int alertMsgLength = fr.getStringWidth(alertMsg.toString());
						if (PitClient.getInstance().pitModConfigManager.getConfig().get("alert_center").getAsBoolean()) {
							int chatWidth = (int) ((Minecraft.getMinecraft()).gameSettings.chatWidth * 320.0F);
							int remainingWidth = chatWidth - alertMsgLength;
							int numSpaces = remainingWidth / fr.getCharWidth(' ') / 2;
							for (int i = 0; i < numSpaces; i++) {
								alertMsg.insert(0, " ");
							}
						}
						Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(alertMsg.toString()));
					}
				} else {
					System.out.println(nonce + " Couldn't find owner!");
				}
			}

		});
	}

	public boolean isOnPit() {
		return this.onPit;
	}

	public void setOnPit(boolean onPit) {
		this.onPit = onPit;
	}

	public void setCurrentPrestigeGold(float gold) {
		this.currentPrestigeGold = gold;
	}

	public void setCurrentPrestigeGoldMax(int pres) {
		this.currentPrestigeGoldMax = getGoldReqForPres(pres);
	}

	public float getCurrentPrestigeGold() {
		return this.currentPrestigeGold;
	}

	public float getCurrentPrestigeGoldMax() {
		return this.currentPrestigeGoldMax;
	}

	public void registerEvent(int minutes, String name) {
		long currMinutes = System.currentTimeMillis() / 60000L;
		long eventTime = currMinutes + minutes;
		JsonObject events = PitClient.getInstance().pitModConfigManager.getEvents();
		if (events.has("" + (eventTime - 1L)) && events.get("" + (eventTime - 1L)).getAsString().equals(name)) {
			return;
		}

		if (events.has("" + (eventTime + 1L)) && events.get("" + (eventTime + 1L)).getAsString().equals(name)) {
			return;
		}

		events.addProperty("" + eventTime, name);
		PitClient.getInstance().pitModConfigManager.saveEvents();
	}

	public void setTelebowCooldown(int seconds) {
		setTelebowCooldown((1000 * seconds), false);
	}

	public void setTelebowCooldown(long millis, boolean forceUpdateOld) {
		long telebowCooldownEndNew = System.currentTimeMillis() + millis;
		if (forceUpdateOld || getTelebowRemaining() <= 0L) {
			this.telebowCooldownEndOld = telebowCooldownEndNew;
		}
		this.telebowCooldownEnd = telebowCooldownEndNew;
	}

	public void setAuraProtectionCooldown() {
		this.auraProtectionEnd = System.currentTimeMillis() + 15000L;
	}

	public long getTelebowRemaining() {
		return getTelebowRemaining(System.currentTimeMillis());
	}

	public long getTelebowRemainingOld() {
		return getTelebowRemainingOld(System.currentTimeMillis());
	}

	public long getTelebowRemaining(long currentTime) {
		return this.telebowCooldownEnd - currentTime;
	}

	public long getTelebowRemainingOld(long currentTime) {
		return this.telebowCooldownEndOld - currentTime;
	}

	public long getAuraProtectionRemaining() {
		return this.auraProtectionEnd - System.currentTimeMillis();
	}

	public void fireTelebow() {
		if (getTelebowRemaining() > 0L) {
			long currentTime = System.currentTimeMillis();
			this.telebowArrowFires.removeIf(time -> (currentTime - time.longValue() > 3000L));
			this.telebowArrowFires.add(Long.valueOf(currentTime));
		}
	}

	public void bowDing() {
		if (getTelebowRemaining() > 0L && !this.telebowArrowFires.isEmpty()) {
			this.telebowArrowFires.remove(this.telebowArrowFires.last());
			this.telebowCooldownEnd -= 3000L;
		}
	}

	private String getPrefix(String player, Boolean current) {
		String prefix = null;
		Boolean vanish = false;
		if (PitClient.scoreboardManager.vanishedNames.contains(player)) vanish = true;
		next: for (Entry<String, JsonElement> entry : PitClient.getInstance().pitModConfigManager.getPlayerWarnings().entrySet()) {
			JsonArray arr = ((JsonElement) entry.getValue()).getAsJsonObject().get("players").getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				String name = arr.get(i).getAsString();
				if (name.equalsIgnoreCase(player)) {
					if (((String) entry.getKey()).equalsIgnoreCase("default")) {
						prefix = "";
						break next;
					}
					prefix = "[" + (String) entry.getKey() + "] ";
					String colourString = ((JsonElement) entry.getValue()).getAsJsonObject().get("colour")
							.getAsString();
					String tracerString = ((JsonElement) entry.getValue()).getAsJsonObject().get("tracer")
							.getAsString();
					tracerMap.put(player, tracerString);
					if (colourString.length() > 0) {
						prefix = "\u00A7" + colourString.toLowerCase().substring(0, 1) + prefix;
						break next;
					}
					break next;
				}
			}
		}
		if (prefix == null && isPlayerNicked(player)) {
			prefix = EnumChatFormatting.DARK_RED + "[Nicked] ";
		}

		if (vanish) {
			prefix = EnumChatFormatting.GRAY + "[SPEC] ";
		}
		return prefix;
	}

	private void sendMessage(String message, String player) {
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		String sound = "pitmod/bell";
		String prefix = getPrefix(player, false);

		if (prefix == null) {
			return;
		}

		StringBuilder alertMsg = new StringBuilder(EnumChatFormatting.DARK_RED.toString() + EnumChatFormatting.BOLD
				+ message + prefix + EnumChatFormatting.RED + player);

		int alertMsgLength = fr.getStringWidth(alertMsg.toString());
		if (PitClient.getInstance().pitModConfigManager.getConfig().get("alert_center").getAsBoolean()) {
			int chatWidth = (int) (Minecraft.getMinecraft().gameSettings.chatWidth * 320.0F);
			int remainingWidth = chatWidth - alertMsgLength;
			int numSpaces = remainingWidth / fr.getCharWidth(' ') / 2;
			for (int i = 0; i < numSpaces; i++) {
				alertMsg.insert(0, " ");
			}
		}
		if (PitClient.getInstance().pitModConfigManager.getConfig().get("alert_sfx").getAsBoolean() && sound.length() > 0) {
			PositionedSoundRecord psr = PositionedSoundRecord.create(new ResourceLocation(sound));
			Minecraft.getMinecraft().getSoundHandler().playSound((ISound) psr);
		}
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(alertMsg.toString()));
	}

	public void processPlayers(Set<String> currentPlayers) {
		boolean alertNicks = PitClient.getInstance().pitModConfigManager.getConfig().get("alert_nicks").getAsBoolean();
		tabListCurrent = new HashSet<>();
		// Make map with players in scoreboard and their prefixes
		for (String player : currentPlayers) {
			String prefix = getPrefix(player, true);
			if (prefix != null) {
				currentPlayerMap.put(player, prefix);
			}
		}

		Set<String> toRemove = new HashSet<>();
		for (String player : this.currentPlayerMap.keySet()) {
			if (!currentPlayers.contains(player)) {
				toRemove.add(player);
			}
		}
		for (String remove : toRemove) {
			currentPlayerMap.remove(remove);
		}

		Set<String> newNicks = new HashSet<>();
		if (alertNicks) {
			for (String player : currentPlayers) {
				if (isPlayerNicked(player) && !nicks.contains(player)) {
					newNicks.add(player);
					nicks.add(player);
				}
			}
			nicks.retainAll(currentPlayers);
		}

		if (PitClient.getInstance().getManager().isOnPit()) {
			for (String player : this.playersOld) {
				if (!currentPlayers.contains(player)) {
					sendMessage(" Player Left! ", player);
				}
			}

			for (String player : currentPlayers) {
				if (!this.playersOld.contains(player) || newNicks.contains(player)) {
					if (vanishMacro) {
						if (Minecraft.getMinecraft().theWorld.getPlayerEntityByName(player) != null) {
							if (Minecraft.getMinecraft().theWorld.getPlayerEntityByName(player).getDisplayName()
									.getFormattedText().contains("\u00A72 " + player)) {
								Minecraft.getMinecraft().thePlayer.sendChatMessage("/l");
								sendMessage("MOD JOINED: ", player);
							}
						}
					}
					sendMessage(" Player Joined! ", player);
					if (isPlayerNicked(player) && denicked.get(player) != null)
						sendMessage(player + " is: ", denicked.get(player));
				}
			}
		}
		playersOld = currentPlayers;
	}

	public int getTelebowLevel(ItemStack item) {
		if (item == null || !item.hasTagCompound()) {
			return 0;
		}
		if (!item.getTagCompound().hasKey("ExtraAttributes", 10)) {
			return 0;
		}
		NBTTagCompound extraAttributes = item.getTagCompound().getCompoundTag("ExtraAttributes");
		if (!extraAttributes.hasKey("CustomEnchants")) {
			return 0;
		}
		NBTTagList enchants = extraAttributes.getTagList("CustomEnchants", 10);
		for (int i = 0; i < enchants.tagCount(); i++) {
			NBTBase base = enchants.get(i);
			if (base instanceof NBTTagCompound) {
				NBTTagCompound enchantTag = (NBTTagCompound) base;
				if (enchantTag.hasKey("Key", 8) && enchantTag.getString("Key").equals("telebow")
						&& enchantTag.hasKey("Level", 99)) {
					return enchantTag.getInteger("Level");
				}
			}
		}
		return 0;
	}

	public void search() {
		if (search) {
			int players = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().size();
			if (players <= searchAmnt + 1) {
				search = false;
				Minecraft.getMinecraft().thePlayer.playSound("random.orb", 1, 1);
				Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
						new ChatComponentText(EnumChatFormatting.GREEN + "Found lobby with " + EnumChatFormatting.AQUA
								+ (players - 1) + EnumChatFormatting.GREEN + " other players."));
				minecart();
			} else {
				Minecraft.getMinecraft().thePlayer.sendChatMessage("/play pit");
			}

		}
	}

	public void minecart() {
		for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
			if (entity instanceof EntityMinecart) {
				PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.GREEN + "Minecart Found!");
				return;
			}
		}
		PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + "Minecart Not Found!");
	}
}
