package ru.mdashlw.hypixel.pit.stats.listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.ClientChatReceivedEvent;

public final class ChatListener {

    private static final String NEW_KEY_PATTERN = "Your new API key is ";

    @SubscribeEvent
    public void onChatMessageReceived(ClientChatReceivedEvent event) {
        String text = event.message.getUnformattedText();

        if (!text.isEmpty()) {
            if (text.startsWith("Your new API key is ")) {
                String key = text.substring("Your new API key is ".length());
                EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;

                thePlayer.addChatMessage((new ChatComponentText("§9[§6PIT§9] ")).appendSibling((new ChatComponentText("§a[Click here] §7to use §9" + key)).setChatStyle((new ChatStyle()).setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentText("§7Update a Hypixel API key in §6Hypixel Pit Stats"))).setChatClickEvent(new ClickEvent(net.minecraft.event.ClickEvent.Action.RUN_COMMAND, "/pit " + key)))));
            }

        }
    }

    public void register() {
        PitClient.EVENT_BUS.register(this);
    }
}
