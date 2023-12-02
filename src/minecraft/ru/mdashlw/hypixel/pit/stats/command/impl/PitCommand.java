package ru.mdashlw.hypixel.pit.stats.command.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import rage.pitclient.PitClient;
import ru.mdashlw.hypixel.api.exception.HypixelApiException;
import ru.mdashlw.hypixel.pit.stats.HypixelPitStats;
import ru.mdashlw.hypixel.pit.stats.command.Command;
import ru.mdashlw.hypixel.pit.stats.gui.GuiStats;

public final class PitCommand extends Command {

    public String getCommandName() {
        return "pit";
    }

    public List getCommandAliases() {
        return Arrays.asList(new String[] { "pitstats", "pitinfo"});
    }

    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage((new ChatComponentText("§9[§6PIT§9] §7Usage: ")).appendSibling((new ChatComponentText("§b/pit <player name>")).setChatStyle((new ChatStyle()).setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/pit ")))));
        } else {
            HypixelPitStats mod = HypixelPitStats.getInstance();
            String playerName = args[0];
            if (PitClient.getInstance().pitModConfigManager.getConfig().get("apikey").getAsString().isEmpty()) {
                sender.addChatMessage((new ChatComponentText("§9[§6PIT§9] §7You have no API key. ")).appendSibling((new ChatComponentText("§c[Click here] §7to run §b/api new")).setChatStyle((new ChatStyle()).setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§7Regenerate a Hypixel API key"))).setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/api new")))));
            } else {
                sender.addChatMessage((new ChatComponentText("§9[§6PIT§9] §7Requesting stats of ")).appendSibling((new ChatComponentText("§d" + playerName)).setChatStyle((new ChatStyle()).setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§7Click to open §fPitPanda"))).setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://pitpanda.rocks/players/" + playerName)))).appendText("§7..."));
                mod.getHypixelAPI().getPlayerByNameAsync(playerName).thenAccept(player -> {
                    if (player == null) {
                        sender.addChatMessage(new ChatComponentText("§9[§6PIT§9] §7Player §c" + playerName + " §7does not exist."));
                        return;
                    } else {
                        String uuid = player.getUUID();

                        mod.getHypixelAPI().getGuildByPlayerAsync(uuid).exceptionally(exception -> {
                        	HypixelPitStats.getLogger().error("Failed to fetch guild of player {}", new Object[]{playerName, exception});
                        	return null;
                        })
                        .thenAccept((guild) -> {
                            sender.addChatMessage((new ChatComponentText("§9[§6PIT§9] " + player.getFormattedName() + " §8- ")).appendSibling((new ChatComponentText("§7[Plancke] ")).setChatStyle((new ChatStyle()).setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§7Check general stats of the player on §bPlancke"))).setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://plancke.io/hypixel/player/stats/" + uuid)))).appendSibling((new ChatComponentText("§7[PitPanda] ")).setChatStyle((new ChatStyle()).setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§7Check The Pit stats of the player on §bPitPanda"))).setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://pitpanda.rocks/players/" + uuid)))).appendSibling((new ChatComponentText("§7[SkyCrypt] ")).setChatStyle((new ChatStyle()).setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§7Check SkyBlock stats of the player on §bSkyCrypt"))).setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://sky.shiiyu.moe/stats/" + uuid)))).appendSibling((new ChatComponentText("§7[NameMC] ")).setChatStyle((new ChatStyle()).setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText("§7Check name history of the player on §bNameMC"))).setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://namemc.com/profile/" + uuid)))));

                            GuiStats screen;

                            try {
                                screen = GuiStats.create(player, guild);
                            } catch (Throwable throwable) {
                                HypixelPitStats.getLogger().error("Failed to create gui stats for player {}", new Object[] { playerName, throwable});
                                sender.addChatMessage(new ChatComponentText("§9[§6PIT§9] §7Failed to create a stats gui."));
                                return;
                            }

                            Minecraft.getMinecraft().displayGuiScreen(screen);
                        });
                    }
                }).exceptionally((exception) -> {
                    if (exception instanceof CompletionException && exception.getCause() != null) {
                        exception = exception.getCause();
                    }

                    HypixelPitStats.getLogger().error("Failed to fetch player by name {}", new Object[] { playerName, exception});
                    if (exception instanceof HypixelApiException) {
                        sender.addChatMessage(new ChatComponentText("§9[§6PIT§9] §7Unexpected API error: §c" + exception.getMessage()));
                    } else {
                        sender.addChatMessage(new ChatComponentText("§9[§6PIT§9] §7Unexpected error: §c" + exception));
                    }

                    return null;
                });
            }
        }
    }

    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length != 1) {
            return null;
        } else {
            NetHandlerPlayClient netHandler = Minecraft.getMinecraft().getNetHandler();
            List players;

            if (netHandler == null) {
                players = Collections.emptyList();
            } else {
                players = (List) netHandler.getPlayerInfoMap().stream().map((info) -> {
                    return info.getGameProfile().getName();
                }).collect(Collectors.toList());
            }

            return CommandBase.getListOfStringsMatchingLastWord(args, players);
        }
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
