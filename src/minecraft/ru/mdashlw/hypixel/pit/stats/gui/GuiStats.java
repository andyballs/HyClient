 package ru.mdashlw.hypixel.pit.stats.gui;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import ru.mdashlw.hypixel.api.data.Guild;
import ru.mdashlw.hypixel.api.data.Player;
import ru.mdashlw.hypixel.api.util.JsonUtils;
import ru.mdashlw.hypixel.api.util.NumberUtils;
import ru.mdashlw.hypixel.pit.stats.util.DurationUtils;
import ru.mdashlw.hypixel.pit.stats.util.ItemStackUtils;

public final class GuiStats extends GuiScreen {

    private static final ResourceLocation INVENTORY_TEXTURE = new ResourceLocation("textures/gui/container/inventory.png");
    private static final ResourceLocation CHEST_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final List slots;
    private final int xSize;
    private final int ySize;
    private int x;
    private int y;

    public GuiStats(List slots) {
        this.slots = slots;
        this.xSize = 176;
        this.ySize = 222;
        this.allowUserInput = true;
    }

    public static GuiStats create(Player player, Guild guild) {
        ArrayList slots = new ArrayList();
        Player.Stats.Pit stats = player.getStats().getPit();
        Player.Stats.Pit.Profile profile = stats.getProfile();
        JsonNode statistics = stats.getStatistics();
        List enderChest = profile.getEnderChest();

        int armor;
        int customItems;
        ItemStack unlocks;

        for (int inventory = 0; inventory < 6; ++inventory) {
            for (armor = 0; armor < 9; ++armor) {
                customItems = armor + inventory * 9;
                unlocks = customItems < enderChest.size() ? (ItemStack) enderChest.get(customItems) : null;
                slots.add(new GuiStats.Slot(8 + armor * 18, 18 + inventory * 18, unlocks));
            }
        }

        List list = profile.getInventory();

        for (armor = 0; armor < 3; ++armor) {
            for (customItems = 0; customItems < 9; ++customItems) {
                int i = 9 + customItems + armor * 9;
                ItemStack passives = i < list.size() ? (ItemStack) list.get(i) : null;

                slots.add(new GuiStats.Slot(8 + customItems * 18, 139 + armor * 18, passives));
            }
        }

        for (armor = 0; armor < 9; ++armor) {
            unlocks = armor < list.size() ? (ItemStack) list.get(armor) : null;
            slots.add(new GuiStats.Slot(8 + armor * 18, 197, unlocks));
        }

        List list1 = profile.getArmor();

        for (customItems = 0; customItems < 4; ++customItems) {
            unlocks = customItems < list1.size() ? (ItemStack) list1.get(customItems) : null;
            slots.add(new GuiStats.SlotArmor(3 - customItems, -16, 141 + (3 - customItems) * 18, unlocks));
        }

        ArrayList arraylist = new ArrayList();

        arraylist.add(ItemStackUtils.withDisplay(ItemStackUtils.withSkullOwner(new ItemStack(Items.skull, 1, 3), player.getName()), player.getFormattedName(), Arrays.asList(new String[] { "§7Hypixel Level: §6" + player.getLevel(), String.format(Locale.US, "§7Achievement Points: §e%,d", new Object[] { Integer.valueOf(player.getAchievementPoints())}), "§7Guild: §b" + (guild == null ? "None" : guild.getName()), String.format(Locale.US, "§7Karma: §d%,d", new Object[] { Integer.valueOf(player.getKarma())}), "", "§7Status: " + (player.getLastLogin() != 0L && player.getLastLogout() != 0L ? (player.isOnline() ? "§aOnline" : "§cOffline") : "§cUnknown"), player.isOnline() ? "§7For " + (player.getLastLogin() == 0L ? "§cUnknown" : "§b" + DurationUtils.format(Duration.ofMillis(System.currentTimeMillis() - player.getLastLogin()))) : "§7Last Online: " + (player.getLastLogout() == 0L ? "§cUnknown" : "§b" + DurationUtils.format(Duration.ofMillis(System.currentTimeMillis() - player.getLastLogout())) + " ago")})));
        arraylist.add(ItemStackUtils.withDisplay(new ItemStack(Items.name_tag), profile.getFormattedLevel() + ' ' + player.getColoredName(), Arrays.asList(new String[] { String.format(Locale.US, "§7Gold: §6%,dg", new Object[] { Integer.valueOf(profile.getCash())}), String.format(Locale.US, "§7Total XP: §b%,d XP", new Object[] { Integer.valueOf(profile.getXP())}), "", String.format(Locale.US, "§7Kills: §c%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "kills"))}), String.format(Locale.US, "§7Hours played: §e%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "playtime_minutes") / 60)}), "", profile.getBounty() == 0 ? null : String.format(Locale.US, "§7Bounty: §6%,dg", new Object[] { Integer.valueOf(profile.getBounty())}), profile.getBounty() == 0 ? null : "", String.format(Locale.US, "§7Renown: §e%,d", new Object[] { Integer.valueOf(profile.getRenown())})})));
        Map map = profile.getUnlocks();
        ArrayList passives = new ArrayList();

        Player.Stats.Pit.getUpgradeNames().forEach((key, name) -> {
            int tier = ((Integer)map.getOrDefault(key, Integer.valueOf(-1))).intValue();
            if (tier == -1) {
              passives.add("§9" + name + ": §cLocked");
            } else {
              passives.add("§9" + name + ": §e" + NumberUtils.toRomanNumeral(tier + 1));
              passives.addAll(((List<Collection<? extends String>>)Player.Stats.Pit.getUpgradeDescriptions().get(key)).get(tier));
            } 
            passives.add("");
          });
        passives.remove(passives.size() - 1);
        arraylist.add(ItemStackUtils.withDisplay(new ItemStack(Items.cake), "§aPassives", passives));
        arraylist.add((Object) null);
        arraylist.add((Object) null);
        arraylist.add(ItemStackUtils.withDisplay(ItemStackUtils.withNoAttributeModifiers(new ItemStack(Items.iron_sword)), "§cOffensive Stats", Arrays.asList(new String[] { String.format(Locale.US, "§7Kills: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "kills"))}), String.format(Locale.US, "§7Assists: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "assists"))}), String.format(Locale.US, "§7Sword Hits: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "sword_hits"))}), String.format(Locale.US, "§7Arrows Shot: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "arrows_fired"))}), String.format(Locale.US, "§7Arrow Hits: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "arrow_hits"))}), "", String.format(Locale.US, "§7Damage Dealt: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "damage_dealt"))}), String.format(Locale.US, "§7Melee Damage Dealt: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "melee_damage_dealt"))}), String.format(Locale.US, "§7Bow Damage Dealt: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "bow_damage_dealt"))}), "", String.format(Locale.US, "§7Highest Streak: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "max_streak"))})})));
        arraylist.add(ItemStackUtils.withDisplay(new ItemStack(Items.iron_chestplate), "§9Defensive Stats", Arrays.asList(new String[] { String.format(Locale.US, "§7Deaths: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "deaths"))}), "", String.format(Locale.US, "§7Damage Taken: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "damage_received"))}), String.format(Locale.US, "§7Melee Damage Taken: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "melee_damage_received"))}), String.format(Locale.US, "§7Bow Damage Taken: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "bow_damage_received"))})})));
        arraylist.add(ItemStackUtils.withDisplay(new ItemStack(Items.wheat), "§ePerformance Stats", Arrays.asList(new String[] { String.format(Locale.US, "§7XP: §b%,d", new Object[] { Integer.valueOf(profile.getXP())}), "", String.format(Locale.US, "§7K/D: §a%.3f", new Object[] { Float.valueOf(NumberUtils.ratio(JsonUtils.getOptionalInt(statistics, "kills"), JsonUtils.getOptionalInt(statistics, "deaths")))}), String.format(Locale.US, "§7K+A/D: §a%.3f", new Object[] { Float.valueOf(NumberUtils.ratio(JsonUtils.getOptionalInt(statistics, "kills") + JsonUtils.getOptionalInt(statistics, "assists"), JsonUtils.getOptionalInt(statistics, "deaths")))}), "", String.format(Locale.US, "§7Damage dealt/taken: §a%.3f", new Object[] { Float.valueOf(NumberUtils.ratio(JsonUtils.getOptionalInt(statistics, "damage_dealt"), JsonUtils.getOptionalInt(statistics, "damage_received")))}), String.format(Locale.US, "§7Arrows hit/shot: §a%.3f", new Object[] { Float.valueOf(NumberUtils.ratio(JsonUtils.getOptionalInt(statistics, "arrow_hits"), JsonUtils.getOptionalInt(statistics, "arrows_fired")))}), "", String.format(Locale.US, "§7Hours played: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "playtime_minutes") / 60)}), String.format(Locale.US, "§7Gold/hour: §a%,.3f", new Object[] { Float.valueOf(NumberUtils.ratio(JsonUtils.getOptionalInt(statistics, "cash_earned"), JsonUtils.getOptionalInt(statistics, "playtime_minutes") / 60))}), String.format(Locale.US, "§7K+A/hour: §a%.3f", new Object[] { Float.valueOf(NumberUtils.ratio(JsonUtils.getOptionalInt(statistics, "kills") + JsonUtils.getOptionalInt(statistics, "assists"), JsonUtils.getOptionalInt(statistics, "playtime_minutes") / 60))})})));
        arraylist.add(ItemStackUtils.withDisplay(new ItemStack(Blocks.obsidian), "§dMiscellaneous Stats", Arrays.asList(new String[] { String.format(Locale.US, "§7Left Clicks: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "left_clicks"))}), String.format(Locale.US, "§7Gold Earned: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "cash_earned"))}), String.format(Locale.US, "§7Diamond Items Purchased: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "diamond_items_purchased"))}), String.format(Locale.US, "§7Chat Messages: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "chat_messages"))}), "", String.format(Locale.US, "§7Blocks Placed: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "blocks_placed"))}), String.format(Locale.US, "§7Blocks Broken: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "blocks_broken"))}), "", String.format(Locale.US, "§7Jumps into Pit: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "jumped_into_pit"))}), String.format(Locale.US, "§7Launcher Launches: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "launched_by_launchers"))}), "", String.format(Locale.US, "§7Golden Apples Eaten: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "gapple_eaten"))}), String.format(Locale.US, "§7Golden Heads Eaten: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "ghead_eaten"))}), "", String.format(Locale.US, "§7Lava Buckets Emptied: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "lava_bucket_emptied"))}), String.format(Locale.US, "§7Fishing Rods Launched: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "fishing_rod_launched"))}), "", String.format(Locale.US, "§7Contracts Completed: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "contracts_completed"))}), "", String.format(Locale.US, "§7Wheat Farmed: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "wheat_farmed"))}), String.format(Locale.US, "§7Gold from Farming: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "gold_from_farming"))}), String.format(Locale.US, "§7Gold from Selling Fish: §cN/A", new Object[0]), "", String.format(Locale.US, "§7King\'s Quest Completed: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "king_quest_completion"))}), String.format(Locale.US, "§7Sewer Treasures Found: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "sewer_treasures_found"))}), "", String.format(Locale.US, "§7Night Quest Completed: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "night_quests_completed"))}), "", String.format(Locale.US, "§7Fished Anything: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "fished_anything"))}), String.format(Locale.US, "§7Fished Fish: §a%,d", new Object[] { Integer.valueOf(JsonUtils.getOptionalInt(statistics, "fishes_fished"))})})));

        for (int row = 0; row < 9; ++row) {
            ItemStack itemStack = row < arraylist.size() ? (ItemStack) arraylist.get(row) : null;

            slots.add(new GuiStats.Slot(8 + row * 18, -13, itemStack));
        }

        return new GuiStats(slots);
    }

    public void initGui() {
        super.initGui();
        this.x = (this.width - this.xSize) / 2;
        this.y = (this.height - this.ySize) / 2;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GuiStats.INVENTORY_TEXTURE);
        this.drawTexturedModalRect(this.x - 24, this.y + 133, 0, 0, 25, 80);
        this.drawTexturedModalRect(this.x - 24, this.y + 213, 0, 160, 25, 6);
        this.mc.getTextureManager().bindTexture(GuiStats.CHEST_TEXTURE);
        this.drawTexturedModalRect(this.x, this.y + 4, 0, 4, this.xSize, 121);
        this.drawTexturedModalRect(this.x, this.y + 125, 0, 126, this.xSize, 96);
        this.drawTexturedModalRect(this.x, this.y - 31, 0, 0, this.xSize, 35);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) this.x, (float) this.y, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GuiStats.Slot hoveredSlot = null;
        Iterator iterator = this.slots.iterator();

        while (iterator.hasNext()) {
            GuiStats.Slot slot = (GuiStats.Slot) iterator.next();

            this.drawSlot(slot);
            if (hoveredSlot == null && this.isMouseOverSlot(slot, mouseX, mouseY)) {
                hoveredSlot = slot;
                int x = slot.x;
                int y = slot.y;

                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(x, y, x + 16, y + 16, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }

        RenderHelper.disableStandardItemLighting();
        this.fontRendererObj.drawString("The Hypixel Pit", 8, -25, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.enderchest", new Object[0]), 8, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 94, 4210752);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.popMatrix();
        if (hoveredSlot != null && hoveredSlot.hasItemStack()) {
            this.renderToolTip(hoveredSlot.getItemStack(), mouseX, mouseY);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }

    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen((GuiScreen) null);
        }

    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawSlot(GuiStats.Slot slot) {
        int x = slot.x;
        int y = slot.y;
        ItemStack itemStack = slot.getItemStack();
        boolean render = true;

        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        if (itemStack == null) {
            TextureAtlasSprite sprite = slot.getBackgroundSprite();

            if (sprite != null) {
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.drawTexturedModalRect(x, y, sprite, 16, 16);
                GlStateManager.enableLighting();
                render = false;
            }
        }

        if (render) {
            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(itemStack, x, y);
            this.itemRender.renderItemOverlays(this.fontRendererObj, itemStack, x, y);
        }

        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    public boolean isMouseOverSlot(GuiStats.Slot slot, int mouseX, int mouseY) {
        return this.isPointInRegion(slot.x, slot.y, 16, 16, mouseX, mouseY);
    }

    public boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
        pointX -= this.x;
        pointY -= this.y;
        return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
    }

    public static final class SlotArmor extends GuiStats.Slot {

        private final int index;

        public SlotArmor(int index, int x, int y, ItemStack itemStack) {
            super(x, y, itemStack);
            this.index = index;
        }

        public String getTexture() {
            return ItemArmor.EMPTY_SLOT_NAMES[this.index];
        }

        public int getIndex() {
            return this.index;
        }
    }

    public static class Slot {

        private final int x;
        private final int y;
        private final ItemStack itemStack;

        public Slot(int x, int y, ItemStack itemStack) {
            this.x = x;
            this.y = y;
            this.itemStack = itemStack;
        }

        public String getTexture() {
            return null;
        }

        public final TextureAtlasSprite getBackgroundSprite() {
            String textureName = this.getTexture();

            return textureName == null ? null : Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureName);
        }

        public final boolean hasItemStack() {
            return this.itemStack != null;
        }

        public final int getX() {
            return this.x;
        }

        public final int getY() {
            return this.y;
        }

        public final ItemStack getItemStack() {
            return this.itemStack;
        }
    }
}
