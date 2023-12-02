package ru.mdashlw.hypixel.api.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import net.hypixel.api.util.ILeveling;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import ru.mdashlw.hypixel.api.util.JsonUtils;
import ru.mdashlw.hypixel.api.util.NumberUtils;
import ru.mdashlw.hypixel.api.util.PitLeveling;
import ru.mdashlw.hypixel.pit.stats.HypixelPitStats;

public final class Player {

    private final JsonNode data;

    public Player(JsonNode data) {
        this.data = data;
    }

    public String getUUID() {
        return this.data.get("uuid").asText();
    }

    public String getName() {
        String name = JsonUtils.getOptionalText(this.data, "displayname");

        if (name != null) {
            return name;
        } else {
            name = JsonUtils.getOptionalText(this.data, "playername");
            if (name != null) {
                return name;
            } else {
                name = JsonUtils.getOptionalText(this.data, "username");
                return name;
            }
        }
    }

    public Player.Rank getRank() {
        String rank = JsonUtils.getOptionalText(this.data, "rank");

        if (rank != null && !rank.equals("NORMAL")) {
            return Player.Rank.valueOf(rank);
        } else {
            rank = JsonUtils.getOptionalText(this.data, "monthlyPackageRank");
            if (rank != null && !rank.equals("NONE")) {
                return Player.Rank.valueOf(rank);
            } else {
                rank = JsonUtils.getOptionalText(this.data, "newPackageRank");
                if (rank != null && !rank.equals("NONE")) {
                    return Player.Rank.valueOf(rank);
                } else {
                    rank = JsonUtils.getOptionalText(this.data, "packageRank");
                    return rank != null && !rank.equals("NONE") ? Player.Rank.valueOf(rank) : Player.Rank.NORMAL;
                }
            }
        }
    }

    public String getFormattedName() {
        String name = this.getName();
        String customPrefix = JsonUtils.getOptionalText(this.data, "prefix");

        if (customPrefix != null) {
            return customPrefix + ' ' + name;
        } else {
            Player.Rank rank = this.getRank();
            String rankColor = EnumChatFormatting.valueOf(JsonUtils.getOptionalText(this.data, "monthlyRankColor", "GOLD")).toString();
            String rankPlusColor = EnumChatFormatting.valueOf(JsonUtils.getOptionalText(this.data, "rankPlusColor", "RED")).toString();
            String prefix = rank.getPrefix().replace("@", rankColor).replace("$", rankPlusColor);

            return prefix + name;
        }
    }

    public String getColoredName() {
        String name = this.getName();
        Player.Rank rank = this.getRank();

        return rank.getColor() + name;
    }

    public int getLevel() {
        int networkExp = JsonUtils.getOptionalInt(this.data, "networkExp");
        int networkLevel = JsonUtils.getOptionalInt(this.data, "networkLevel");
        double exp = (double) networkExp + ILeveling.getTotalExpToFullLevel((double) (networkLevel + 1));

        return (int) ILeveling.getLevel(exp);
    }

    public int getAchievementPoints() {
        return JsonUtils.getOptionalInt(this.data, "achievementPoints");
    }

    public int getKarma() {
        return JsonUtils.getOptionalInt(this.data, "karma");
    }

    public long getLastLogin() {
        return JsonUtils.getOptionalLong(this.data, "lastLogin");
    }

    public long getLastLogout() {
        return JsonUtils.getOptionalLong(this.data, "lastLogout");
    }

    public boolean isOnline() {
        long lastLogin = this.getLastLogin();
        long lastLogout = this.getLastLogout();

        return lastLogin != 0L && lastLogout != 0L && lastLogin > lastLogout;
    }

    public Player.Stats getStats() {
        return new Player.Stats(JsonUtils.getOptionalObject(this.data, "stats"));
    }

    public static final class Stats {

        private final JsonNode data;

        public Stats(JsonNode data) {
            this.data = data;
        }

        public Player.Stats.Pit getPit() {
            return new Player.Stats.Pit(JsonUtils.getOptionalObject(this.data, "Pit"));
        }

        public static final class Pit {

            private static final Map UPGRADE_NAMES = new LinkedHashMap();
            private static final Map UPGRADE_DESCRIPTIONS = new HashMap();
            private static final String[] LEVEL_COLORS = new String[] { "§7", "§9", "§3", "§2", "§a", "§e", "§6§l", "§c§l", "§4§l", "§5§l", "§d§l", "§f§l", "§b§l"};
            private static final String[] PRESTIGE_COLORS = new String[] { "§7", "§9", "§9", "§9", "§9", "§e", "§e", "§e", "§e", "§e", "§6", "§6", "§6", "§6", "§6", "§c", "§c", "§c", "§c", "§c", "§5", "§5", "§5", "§5", "§5", "§d", "§d", "§d", "§d", "§d", "§f", "§f", "§f", "§f", "§f", "§b"};
            private final JsonNode data;

            public Pit(JsonNode data) {
                this.data = data;
            }

            public static Map getUpgradeNames() {
                return Player.Stats.Pit.UPGRADE_NAMES;
            }

            public static Map getUpgradeDescriptions() {
                return Player.Stats.Pit.UPGRADE_DESCRIPTIONS;
            }

            public Player.Stats.Pit.Profile getProfile() {
                return new Player.Stats.Pit.Profile(JsonUtils.getOptionalObject(this.data, "profile"));
            }

            public JsonNode getStatistics() {
                return JsonUtils.getOptionalObject(this.data, "pit_stats_ptl");
            }

            static {
                Player.Stats.Pit.UPGRADE_NAMES.put("xp_boost", "XP Boost");
                Player.Stats.Pit.UPGRADE_NAMES.put("cash_boost", "Gold Boost");
                Player.Stats.Pit.UPGRADE_NAMES.put("melee_damage", "Melee Damage");
                Player.Stats.Pit.UPGRADE_NAMES.put("bow_damage", "Bow Damage");
                Player.Stats.Pit.UPGRADE_NAMES.put("damage_reduction", "Damage Reduction");
                Player.Stats.Pit.UPGRADE_NAMES.put("build_battler", "Build Battler");
                Player.Stats.Pit.UPGRADE_NAMES.put("el_gato", "El Gato");
                Player.Stats.Pit.UPGRADE_DESCRIPTIONS.put("xp_boost", Arrays.asList(new List[] { Arrays.asList(new String[] { "§7Earn §b+10% XP §7from all", "§7sources."}), Arrays.asList(new String[] { "§7Earn §b+20% XP §7from all", "§7sources."}), Arrays.asList(new String[] { "§7Earn §b+30% XP §7from all", "§7sources."}), Arrays.asList(new String[] { "§7Earn §b+40% XP §7from all", "§7sources."}), Arrays.asList(new String[] { "§7Earn §b+50% XP §7from all", "§7sources."}), Arrays.asList(new String[] { "§7Earn §b+60% XP §7from all", "§7sources."})}));
                Player.Stats.Pit.UPGRADE_DESCRIPTIONS.put("cash_boost", Arrays.asList(new List[] { Arrays.asList(new String[] { "§7Earn §6+10% gold (g) §7from", "§7kills and coin pickups."}), Arrays.asList(new String[] { "§7Earn §6+20% gold (g) §7from", "§7kills and coin pickups."}), Arrays.asList(new String[] { "§7Earn §6+30% gold (g) §7from", "§7kills and coin pickups."}), Arrays.asList(new String[] { "§7Earn §6+40% gold (g) §7from", "§7kills and coin pickups."}), Arrays.asList(new String[] { "§7Earn §6+50% gold (g) §7from", "§7kills and coin pickups."}), Arrays.asList(new String[] { "§7Earn §6+60% gold (g) §7from", "§7kills and coin pickups."})}));
                Player.Stats.Pit.UPGRADE_DESCRIPTIONS.put("melee_damage", Arrays.asList(new List[] { Collections.singletonList("§7Deal §c+1% §7melee damage."), Collections.singletonList("§7Deal §c+2% §7melee damage."), Collections.singletonList("§7Deal §c+3% §7melee damage."), Collections.singletonList("§7Deal §c+4% §7melee damage."), Collections.singletonList("§7Deal §c+5% §7melee damage."), Collections.singletonList("§7Deal §c+6% §7melee damage.")}));
                Player.Stats.Pit.UPGRADE_DESCRIPTIONS.put("bow_damage", Arrays.asList(new List[] { Collections.singletonList("§7Deal §c+3% §7bow damage."), Collections.singletonList("§7Deal §c+6% §7bow damage."), Collections.singletonList("§7Deal §c+9% §7bow damage."), Collections.singletonList("§7Deal §c+12% §7bow damage."), Collections.singletonList("§7Deal §c+15% §7bow damage."), Collections.singletonList("§7Deal §c+18% §7bow damage.")}));
                Player.Stats.Pit.UPGRADE_DESCRIPTIONS.put("damage_reduction", Arrays.asList(new List[] { Collections.singletonList("§7Receive §9-1% §7damage."), Collections.singletonList("§7Receive §9-2% §7damage."), Collections.singletonList("§7Receive §9-3% §7damage."), Collections.singletonList("§7Receive §9-4% §7damage."), Collections.singletonList("§7Receive §9-5% §7damage."), Collections.singletonList("§7Receive §9-6% §7damage.")}));
                Player.Stats.Pit.UPGRADE_DESCRIPTIONS.put("build_battler", Arrays.asList(new List[] { Arrays.asList(new String[] { "§7Your blocks stay §a+60%", "§7longer."}), Arrays.asList(new String[] { "§7Your blocks stay §a+120%", "§7longer."}), Arrays.asList(new String[] { "§7Your blocks stay §a+180%", "§7longer."}), Arrays.asList(new String[] { "§7Your blocks stay §a+240%", "§7longer."}), Arrays.asList(new String[] { "§7Your blocks stay §a+300%", "§7longer."}), Arrays.asList(new String[] { "§7Your blocks stay §a+360%", "§7longer."})}));
                Player.Stats.Pit.UPGRADE_DESCRIPTIONS.put("el_gato", Arrays.asList(new List[] { Arrays.asList(new String[] { "§dFirst kill §7each life", "§7rewards §6+§65g §b+5 XP§7."}), Arrays.asList(new String[] { "§dFirst 2 kills §7each life", "§7reward §6+§65g §b+5 XP§7."}), Arrays.asList(new String[] { "§dFirst 3 kills §7each life", "§7reward §6+§65g §b+5 XP§7."}), Arrays.asList(new String[] { "§dFirst 4 kills §7each life", "§7reward §6+§65g §b+5 XP§7."}), Arrays.asList(new String[] { "§dFirst 5 kills §7each life", "§7reward §6+§65g §b+5 XP§7."}), Arrays.asList(new String[] { "§dFirst 6 kills §7each life", "§7reward §6+§65g §b+5 XP§7."})}));
            }

            public static final class Profile {

                private final JsonNode data;

                public Profile(JsonNode data) {
                    this.data = data;
                }

                public List parseInventory(String field) {
                    JsonNode data = this.data.get(field);

                    if (data == null) {
                        return Collections.emptyList();
                    } else {
                        byte[] bytes = JsonUtils.getByteArray(data, "data");

                        if (bytes == null) {
                            return Collections.emptyList();
                        } else {
                            NBTTagCompound tag;

                            try {
                                tag = CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes));
                            } catch (IOException ioexception) {
                                HypixelPitStats.getLogger().error("Failed to parse inventory data", ioexception);
                                return Collections.emptyList();
                            }

                            ArrayList itemStacks = new ArrayList();
                            NBTTagList itemListTag = tag.getTagList("i", 10);

                            for (int i = 0; i < itemListTag.tagCount(); ++i) {
                                NBTTagCompound itemTag = itemListTag.getCompoundTagAt(i);

                                itemStacks.add(ItemStack.loadItemStackFromNBT(itemTag));
                            }

                            return itemStacks;
                        }
                    }
                }

                public List getArmor() {
                    return this.parseInventory("inv_armor");
                }

                public List getInventory() {
                    return this.parseInventory("inv_contents");
                }

                public List getEnderChest() {
                    return this.parseInventory("inv_enderchest");
                }

                public int getCash() {
                    return (int) Math.round(JsonUtils.getOptionalDouble(this.data, "cash"));
                }

                public int getXP() {
                    return JsonUtils.getOptionalInt(this.data, "xp");
                }

                public int getRenown() {
                    return JsonUtils.getOptionalInt(this.data, "renown");
                }

                public int getPrestige() {
                    JsonNode data = this.data.get("prestiges");

                    return data == null ? 0 : data.size();
                }

                public Map getUnlocks() {
                    JsonNode data = this.data.get("unlocks_" + this.getPrestige());

                    if (data == null) {
                        data = this.data.get("unlocks");
                        if (data == null) {
                            return Collections.emptyMap();
                        }
                    }

                    HashMap unlocks = new HashMap();
                    Iterator iterator = data.iterator();

                    while (iterator.hasNext()) {
                        JsonNode node = (JsonNode) iterator.next();

                        unlocks.put(node.get("key").asText(), Integer.valueOf(node.get("tier").asInt()));
                    }

                    return unlocks;
                }

                public int getLevel() {
                    return PitLeveling.getLevel(this.getPrestige(), this.getXP());
                }

                public String getFormattedLevel() {
                    int prestige = this.getPrestige();
                    int level = this.getLevel();
                    String levelColor = Player.Stats.Pit.LEVEL_COLORS[(int) Math.floor((double) level / 10.0D)];

                    if (prestige == 0) {
                        return "§7[" + levelColor + level + "§r§7]";
                    } else {
                        String prestigeColor = Player.Stats.Pit.PRESTIGE_COLORS[prestige];

                        return prestigeColor + "[§e" + NumberUtils.toRomanNumeral(prestige) + prestigeColor + '-' + levelColor + level + "§7" + prestigeColor + ']';
                    }
                }

                public int getBounty() {
                    JsonNode data = this.data.get("bounties");

                    if (data != null && !data.isEmpty()) {
                        int bounty = 0;

                        JsonNode node;

                        for (Iterator iterator = data.iterator(); iterator.hasNext(); bounty += node.get("amount").asInt()) {
                            node = (JsonNode) iterator.next();
                        }

                        return bounty;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

    public static enum Rank {

        NORMAL("§7", "§7"), VIP("§a[VIP] ", "§a"), VIP_PLUS("§a[VIP§6+§a] ", "§a"), MVP("§b[MVP] ", "§b"), MVP_PLUS("§b[MVP$+§b] ", "§b"), SUPERSTAR("@[MVP$++@] ", "§6"), YOUTUBER("§c[§fYOUTUBE§c] ", "§c"), JR_HELPER("§9[JR HELPER] ", "§9"), HELPER("§9[HELPER] ", "§9"), MODERATOR("§2[MOD] ", "§2"), ADMIN("§c[ADMIN] ", "§c");

        private final String prefix;
        private final String color;

        private Rank(String prefix, String color) {
            this.prefix = prefix;
            this.color = color;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public String getColor() {
            return this.color;
        }
    }
}
