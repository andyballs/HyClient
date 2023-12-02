package rage.pitclient.listener;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Mouse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.ClientChatReceivedEvent;
import rage.pitclient.events.InputEvent;
import rage.pitclient.events.PlayerInteractEvent;
import rage.pitclient.events.PlayerUseItemEvent;
import rage.pitclient.events.RenderGameOverlayEvent;
import rage.pitclient.events.RenderGameOverlayEvent.ElementType;
import rage.pitclient.events.SoundEvent;
import rage.pitclient.events.TickEvent;
import rage.pitclient.util.MiscUtils;
import rage.pitclient.util.RenderUtils;
import rage.pitclient.util.StringUtils;

public class EventListener {
	
	@SubscribeEvent
	public void onDrawOverlay(RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.TEXT)
			return;
		if (!PitClient.getInstance().getManager().isOnPit())
			return;
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo)
			return;

		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		JsonObject overlays = PitClient.getInstance().pitModConfigManager.getOverlays();
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;

		JsonObject playerList = overlays.get("playerList").getAsJsonObject();
		if (!playerList.get("disable").getAsBoolean()
				&& !(enemiesList.isEmpty() && friendsList.isEmpty() && PitClient.getInstance().getManager().currentPlayerMap.isEmpty() && PitClient.getInstance().getManager().darkMap.isEmpty())) {
			int x = (int) (playerList.get("x").getAsFloat() * scaledResolution.getScaledWidth());
			int y = (int) (playerList.get("y").getAsFloat() * scaledResolution.getScaledHeight());

			int maxLen = 95;
			int height = 20;
			
			HashMap<String,String> renderPlayerMap = new HashMap<>();
			
			for (Entry<String, String> entry : PitClient.getInstance().getManager().currentPlayerMap.entrySet()) {
				
				String name = ScorePlayerTeam.formatPlayerName(Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(entry.getKey()),entry.getKey());
				name = name.replace(EnumChatFormatting.BOLD.toString(), "");
				name = name.substring(0, name.indexOf(entry.getKey()) + entry.getKey().length());	
				
				String str = entry.getValue() + name;
				if (PitClient.getInstance().getManager().denicked.containsValue(entry.getKey())) {
					str += " (" + PitClient.getInstance().getManager().denicked.get(entry.getKey()) + ")";
				}
				int len = fr.getStringWidth(str);
				if (len > maxLen) maxLen = len;
				height += 11;

				renderPlayerMap.put(name, entry.getValue());
			}
			
			for (Entry<String, String> darkUser : PitClient.getInstance().getManager().darkMap.entrySet()) {
				String name = ScorePlayerTeam.formatPlayerName(Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(darkUser.getKey()),darkUser.getKey());
				name = name.replace(EnumChatFormatting.BOLD.toString(), "");
				name = name.substring(0, name.indexOf(darkUser.getKey()) + darkUser.getKey().length());	
				String str = name +  " - " + darkUser.getValue();
				int len = fr.getStringWidth(str);
				if (len > maxLen) maxLen = len;
				height += 11;
				renderPlayerMap.put(EnumChatFormatting.RESET + " - " + darkUser.getValue(), name);
			}
			
			for (String friend : friendsList) {
				String name = ScorePlayerTeam.formatPlayerName(Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(friend),friend);
				name = name.replace(EnumChatFormatting.BOLD.toString(), "");
				name = name.substring(0, name.indexOf(friend) + friend.length());	
				String str = "[FRIEND] " + name;
				int len = fr.getStringWidth(str);
				if (len > maxLen) maxLen = len;
				height += 11;
				renderPlayerMap.put(name, EnumChatFormatting.DARK_BLUE + "[FRIEND] ");
			}
			
			for (String enemy : enemiesList) {
				String name = ScorePlayerTeam.formatPlayerName(Minecraft.getMinecraft().theWorld.getScoreboard().getPlayersTeam(enemy),enemy);
				name = name.replace(EnumChatFormatting.BOLD.toString(), "");
				name = name.substring(0, name.indexOf(enemy) + enemy.length());	
				String str = "[ENEMY] " + name;
				int len = fr.getStringWidth(str);
				if (len > maxLen) maxLen = len;
				height += 11;
				renderPlayerMap.put(name, EnumChatFormatting.DARK_RED + "[ENEMY] ");
			}
			
			maxLen += 5;

			GlStateManager.enableBlend();
			if (playerList.get("show_background").getAsBoolean()) {
				RenderUtils.drawFloatingRect(x, y, maxLen, height);
			}

			String title = playerList.get("custom_text").getAsString().replace("&", "\\u00A7").trim();
			String titleClean = StringUtils.cleanColourNotModifiers(title);
			int titleLength = fr.getStringWidth(title);
			if (playerList.get("text_shadow").getAsBoolean()) {
				for (int xOff = -2; xOff <= 2; xOff++) {
					for (int yOff = -2; yOff <= 2; yOff++) {
						if (xOff * xOff != yOff * yOff) {
							fr.drawString(titleClean, x + maxLen / 2.0F - titleLength / 2.0F + xOff / 2.0F,
									(y + 3) + yOff / 2.0F, 1342177280, false);
						}
					}
				}
			}

			int colour = -1;
			try {
				colour = (int) Long.parseLong(playerList.get("text_colour").getAsString().toLowerCase(), 16);
			} catch (Exception exception) {
			}
			fr.drawString(title, x + maxLen / 2.0F - titleLength / 2.0F, (y + 3), colour, false);

			int currHeight = 16;
			for (Entry<String, String> entry : renderPlayerMap.entrySet()) {
				String str = (String) entry.getValue() + (String) entry.getKey();
				String strClean = EnumChatFormatting.getTextWithoutFormattingCodes(str);
				if (playerList.get("text_shadow").getAsBoolean()) {
					for (int xOff = -2; xOff <= 2; xOff++) {
						for (int yOff = -2; yOff <= 2; yOff++) {
							if (xOff * xOff != yOff * yOff) {
								fr.drawString(strClean, (x + 3) + xOff / 2.0F, (y + currHeight) + yOff / 2.0F,
										1342177280, false);
							}
						}
					}
				}

				int prefixLen = fr.getStringWidth(entry.getValue());

				String prefix = entry.getValue();
				fr.drawString(prefix + EnumChatFormatting.RED + (String) entry.getKey(), x + 3, y + currHeight, -1);
				currHeight += 11;
			}
		}

		long telebowRemaining = PitClient.getInstance().getManager().getTelebowRemaining();
		if (telebowRemaining > 0L) {
			JsonObject telebow = overlays.get("telebow").getAsJsonObject();
			if (!telebow.get("disable").getAsBoolean()) {

				String cooldownText = telebow.get("custom_text").getAsString().replace("&", "\\u00A7")
						+ ((float) (telebowRemaining / 100L) / 10.0F) + "s";
				String cooldownTextClean = StringUtils.cleanColourNotModifiers(cooldownText);
				int cooldownLength = fr.getStringWidth(cooldownText);

				int x = (int) (telebow.get("x").getAsFloat() * scaledResolution.getScaledWidth());
				int y = (int) (telebow.get("y").getAsFloat() * scaledResolution.getScaledHeight());

				GlStateManager.enableBlend();
				if (telebow.get("show_background").getAsBoolean()) {
					RenderUtils.drawFloatingRect(x, y, 5 + cooldownLength, 13);
				}
				if (telebow.get("text_shadow").getAsBoolean()) {
					for (int xOff = -2; xOff <= 2; xOff++) {
						for (int yOff = -2; yOff <= 2; yOff++) {
							if (xOff * xOff != yOff * yOff) {
								fr.drawString(cooldownTextClean, (x + 3) + xOff / 2.0F, (y + 3) + yOff / 2.0F,
										1342177280, false);
							}
						}
					}
				}

				int colour = -1;
				try {
					colour = (int) Long.parseLong(telebow.get("text_colour").getAsString().toLowerCase(), 16);
				} catch (Exception exception) {
				}
				fr.drawString(cooldownText, (x + 3), (y + 3), colour, false);
			}
		}

		long auraRemaining = PitClient.getInstance().getManager().getAuraProtectionRemaining();
		if (auraRemaining > 0L) {
			JsonObject aura = overlays.get("aop").getAsJsonObject();
			if (!aura.get("disable").getAsBoolean()) {

				String cooldownText = aura.get("custom_text").getAsString().replace("&", "\\u00A7")
						+ ((float) (auraRemaining / 100L) / 10.0F) + "s";
				String cooldownTextClean = StringUtils.cleanColourNotModifiers(cooldownText);
				int cooldownLength = fr.getStringWidth(cooldownText);

				int x = (int) (aura.get("x").getAsFloat() * scaledResolution.getScaledWidth());
				int y = (int) (aura.get("y").getAsFloat() * scaledResolution.getScaledHeight());

				GlStateManager.enableBlend();
				if (aura.get("show_background").getAsBoolean()) {
					RenderUtils.drawFloatingRect(x, y, 5 + cooldownLength, 13);
				}
				if (aura.get("text_shadow").getAsBoolean()) {
					for (int xOff = -2; xOff <= 2; xOff++) {
						for (int yOff = -2; yOff <= 2; yOff++) {
							if (xOff * xOff != yOff * yOff) {
								fr.drawString(cooldownTextClean, (x + 3) + xOff / 2.0F, (y + 3) + yOff / 2.0F,
										1342177280, false);
							}
						}
					}
				}
				
				int colour = -1;
				try {
					colour = (int) Long.parseLong(aura.get("text_colour").getAsString().toLowerCase(), 16);
				} catch (Exception exception) {
				}
				fr.drawString(cooldownText, (x + 3), (y + 3), colour, false);
			}
		}
		
		fr.drawStringWithShadow("Players: " + EnumChatFormatting.GOLD + currentPlayers + "/81", Minecraft.getMinecraft().displayWidth/4+10, Minecraft.getMinecraft().displayHeight/2-48, Color.LIGHT_GRAY.getRGB());
		
		GlStateManager.enableDepth();
	}

	@SubscribeEvent
	public void onSound(SoundEvent.SoundSourceEvent event) {
		if (event.sound == null) return;
		if (event.sound.getSoundLocation().getResourcePath().equals("random.successful_hit")) {
			PitClient.getInstance().getManager().bowDing();
		} else if (event.sound.getSoundLocation().getResourcePath().equals("mob.villager.no")) {
			long currentTime = System.currentTimeMillis();
			long old = PitClient.getInstance().getManager().getTelebowRemainingOld(currentTime);
			long remaining = PitClient.getInstance().getManager().getTelebowRemaining(currentTime);
			if (old != remaining) {
				PitClient.getInstance().getManager().setTelebowCooldown(old, false);
			} else {
				PitClient.getInstance().getManager().setTelebowCooldown(0L, true);
			}
		}
	}

	public void onGuiChat(ClientChatReceivedEvent e) {
		String unformatted = StringUtils.cleanColour(e.message.getUnformattedText());
		if (unformatted.startsWith("Your new API key is ")) {
			String key = unformatted.substring("Your new API key is ".length());
			JsonObject config = PitClient.getInstance().pitModConfigManager.getConfig();
			config.addProperty("apikey", key);
			PitClient.getInstance().pitModConfigManager.saveConfig();
		}
	}

	final Pattern SERVER_BRAND_PATTERN = Pattern.compile("(.+) <- (?:.+)");
	final String HYPIXEL_SERVER_BRAND = "BungeeCord (Hypixel)";

	Pattern teamNamePattern = Pattern.compile("[a-z]{4}\\d+");
	private static final Pattern eventPattern = Pattern.compile(EnumChatFormatting.GRAY + "\\+"
			+ EnumChatFormatting.YELLOW + "(\\d+)h(\\d+)m" + EnumChatFormatting.GRAY + ": (.+)");

	public HashMap<String, Integer> notifiedEvents = new HashMap<>();
	public long lastUpdate = 0L;
	public long lastLongUpdate = 0L;
	public long lastSearch = 0L;
	
	private int currentPlayers;

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if ((PitClient.getInstance()).guiToOpen != null) {
			Minecraft.getMinecraft().displayGuiScreen((PitClient.getInstance()).guiToOpen);
			(PitClient.getInstance()).guiToOpen = null;
		}
		if (Minecraft.getMinecraft().theWorld == null) return;
		
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastLongUpdate > 10000L && (Minecraft.getMinecraft()).thePlayer != null) {
			lastLongUpdate = currentTime;
			HashMap<String, String> args = new HashMap<>();
			args.put("uuid", (Minecraft.getMinecraft()).thePlayer.getUniqueID().toString().replace("-", ""));
			PitClient.getInstance().getHypixelApi().getHypixelApiAsync(
					PitClient.getInstance().pitModConfigManager.getConfig().get("apikey").getAsString(), "player", args, json -> {
						try {
							JsonObject obj = MiscUtils.getElement((JsonElement) json, "player.stats.Pit.profile").getAsJsonObject();

							float highestGold = 0.0F;
							int pres = 0;
							for (int i = 0; i < 50; i++) {
								if (obj.has("cash_during_prestige_" + i)) {
									highestGold = obj.get("cash_during_prestige_" + i).getAsFloat();
									pres = i;
								}
							}
							PitClient.getInstance().getManager().setCurrentPrestigeGold(highestGold);
							PitClient.getInstance().getManager().setCurrentPrestigeGoldMax(pres);
						} catch (Exception e) {
						}
					});
		}

		if (currentTime - lastSearch > 8000L) {
			lastSearch = currentTime;
			PitClient.getInstance().getManager().search();
		}

		if (currentTime - lastUpdate > 1000L) {
			lastUpdate = currentTime;
			Minecraft mc = Minecraft.getMinecraft();
			
			currentPlayers = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().size();

			PitClient.getInstance().getManager().setOnPit(false);
			if (mc != null && mc.theWorld != null && mc.thePlayer != null) {
				if (!mc.isSingleplayer() && mc.thePlayer.getClientBrand() != null) {
					Matcher matcher = SERVER_BRAND_PATTERN.matcher(mc.thePlayer.getClientBrand());
					if (matcher.find()) {
						if (matcher.group(1).contains("Hypixel BungeeCord")) {
							Scoreboard scoreboard = mc.theWorld.getScoreboard();
							ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
							if (sidebarObjective != null) {
								String objectiveName = sidebarObjective.getDisplayName().replaceAll("(?i)\\u00A7.", "");
								if (objectiveName.equals("THE HYPIXEL PIT")) {
									PitClient.getInstance().getManager().setOnPit(true);
								}
							}
						}
					}
				}
			}
			for (Entry<String, JsonElement> entry : PitClient.getInstance().pitModConfigManager.getEvents().entrySet()) {
				try {
					String eventName = ((JsonElement) entry.getValue()).getAsString();
					long time = 60000L * Long.parseLong(entry.getKey());
					if (time > currentTime - 660000L && time < currentTime - 600000L) {
						if (!this.notifiedEvents.containsKey(entry.getKey())
								|| ((Integer) this.notifiedEvents.get(entry.getKey())).intValue() < 0) {
							this.notifiedEvents.put(entry.getKey(), Integer.valueOf(0));
							(Minecraft.getMinecraft()).thePlayer.addChatMessage(
									(IChatComponent) new ChatComponentText(eventName + " is starting in 10 minutes!"));
							break;
						}
						continue;
					}
					if (time > currentTime - 120000L && time < currentTime - 60000L
							&& (!this.notifiedEvents.containsKey(entry.getKey())
									|| ((Integer) this.notifiedEvents.get(entry.getKey())).intValue() < 1)) {
						this.notifiedEvents.put(entry.getKey(), Integer.valueOf(1));
						(Minecraft.getMinecraft()).thePlayer.addChatMessage(
								(IChatComponent) new ChatComponentText(eventName + " is starting in 1 minute!"));

						break;
					}
				} catch (Exception exception) {
				}
			}
			if ((Minecraft.getMinecraft()).currentScreen instanceof GuiChest) {
				PitClient.getInstance().pitModConfigManager.clearEvents();
				IInventory inv = ((ContainerChest) (Minecraft.getMinecraft()).thePlayer.openContainer)
						.getLowerChestInventory();
				for (int i = 0; i < inv.getSizeInventory(); i++) {
					ItemStack item = inv.getStackInSlot(i);
					if (item != null && item.hasTagCompound()) {
						NBTTagCompound tag = item.getTagCompound();
						if (tag.hasKey("display", 10)) {
							NBTTagCompound display = tag.getCompoundTag("display");
							if (display.hasKey("Lore")) {
								NBTTagList lore = display.getTagList("Lore", 8);
								for (int loreIndex = 0; loreIndex < lore.tagCount(); loreIndex++) {
									String line = lore.getStringTagAt(loreIndex);
									Matcher matcher = eventPattern.matcher(line);
									if (matcher.matches()) {
										String hours = matcher.group(1);
										String minutes = matcher.group(2);
										String eventName = matcher.group(3);

										int timeUntil = 0;
										try {
											timeUntil += Integer.parseInt(hours) * 60;
											timeUntil += Integer.parseInt(minutes);

											PitClient.getInstance().getManager().registerEvent(timeUntil, eventName);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			}
			if ((Minecraft.getMinecraft()).thePlayer != null && (Minecraft.getMinecraft()).theWorld != null) {
				Set<String> players = new HashSet<>();
				friendsList.clear();
				enemiesList.clear();
				for (ScorePlayerTeam team : (Minecraft.getMinecraft()).thePlayer.getWorldScoreboard().getTeams()) {
					if (team.getNameTagVisibility() == EnumVisible.NEVER) continue;
					Matcher matcher = this.teamNamePattern.matcher(team.getTeamName());
					if (matcher.matches()) {
						for (String playerName : team.getMembershipCollection()) {
							if ((Minecraft.getMinecraft()).thePlayer.getName().equals(playerName)) {
								continue;
							}
							if (PitClient.friendManager.isFriend(playerName)) friendsList.add(playerName);
							if (PitClient.friendManager.isEnemy(playerName)) enemiesList.add(playerName);
							players.add(playerName);
						}
					}
				}

				PitClient.getInstance().getManager().processPlayers(players);
			}
		}
	}
	
	private ArrayList<String> friendsList = new ArrayList<>();
	private ArrayList<String> enemiesList = new ArrayList<>();

	@SubscribeEvent
	public void useItem(PlayerInteractEvent event) {
		if (event.entityPlayer == (Minecraft.getMinecraft()).thePlayer) {
			ItemStack item = event.entityPlayer.inventory.getCurrentItem();
			if (item != null && item.getItem() == Items.slime_ball
					&& item.getDisplayName().contains(EnumChatFormatting.GREEN + "Aura of Protection")) {
				PitClient.getInstance().getManager().setAuraProtectionCooldown();
			}
		}
	}

	@SubscribeEvent
	public void useItemLeft(InputEvent.MouseInputEvent event) {
		if (Mouse.getEventButtonState() && Mouse.getEventButton() == 0
				&& (Minecraft.getMinecraft()).currentScreen == null && (Minecraft.getMinecraft()).thePlayer != null) {
			ItemStack item = (Minecraft.getMinecraft()).thePlayer.inventory.getCurrentItem();
			if (item != null && item.getItem() == Items.slime_ball
					&& item.getDisplayName().contains(EnumChatFormatting.GREEN + "Aura of Protection")) {
				PitClient.getInstance().getManager().setAuraProtectionCooldown();
			}
		}
	}

	private static final Pattern telebowCDPattern = Pattern.compile(EnumChatFormatting.YELLOW + "Telebow: "
			+ EnumChatFormatting.RED + "(\\d+)s cooldown!" + EnumChatFormatting.RESET + EnumChatFormatting.RESET);

	@SubscribeEvent
	public void onMessage(ClientChatReceivedEvent event) {
		if (event.type == 2) {
			// System.out.println(event.message.getFormattedText());
			Matcher matcher = telebowCDPattern.matcher(event.message.getFormattedText());
			if (matcher.matches()) {
				String cdS = matcher.group(1);
				try {
					int cd = Integer.parseInt(cdS);
					PitClient.getInstance().getManager().setTelebowCooldown((cd * 1000), true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// Afk warning check
		String message = event.message.getUnformattedText();
		if (message.contains("AFK WARNING!")) {
			Minecraft.getMinecraft().thePlayer.playSound("random.orb", 1, 1);
		}
	}

	@SubscribeEvent
	public void useItemStop(PlayerUseItemEvent.Stop event) {
		if (event.entityPlayer == (Minecraft.getMinecraft()).thePlayer && event.item.getItem() == Items.bow) {
			int telebow = PitClient.getInstance().getManager().getTelebowLevel(event.item);
			if (telebow > 0) {
				PitClient.getInstance().getManager().fireTelebow();
				if ((Minecraft.getMinecraft()).thePlayer.isSneaking())
					if (telebow == 1) {
						PitClient.getInstance().getManager().setTelebowCooldown(90);
					} else if (telebow == 2) {
						PitClient.getInstance().getManager().setTelebowCooldown(45);
					} else {
						PitClient.getInstance().getManager().setTelebowCooldown(20);
					}
			}
		}
	}
	
}
