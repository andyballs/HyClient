package ru.mdashlw.hypixel.pit.stats;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import rage.pitclient.PitClient;
import ru.mdashlw.hypixel.api.HypixelAPI;
import ru.mdashlw.hypixel.pit.stats.command.impl.PitCommand;
import ru.mdashlw.hypixel.pit.stats.listeners.ChatListener;

public final class HypixelPitStats {

    private static HypixelPitStats INSTANCE;
    private static Logger LOGGER;
    private HypixelAPI hypixelAPI;

    public static HypixelPitStats getInstance() {
        return HypixelPitStats.INSTANCE;
    }

    public static Logger getLogger() {
        return HypixelPitStats.LOGGER;
    }

    public void initialize() {
    	INSTANCE = this;
        HypixelPitStats.LOGGER = LogManager.getLogger("Pit Stats");
        this.initHypixelAPI();
        this.registerListeners();
        this.registerCommands();
    }

    public void initHypixelAPI() {
        this.hypixelAPI = new HypixelAPI(PitClient.getInstance().pitModConfigManager.getConfig().get("apikey").getAsString());
    }

    public void registerListeners() {
        (new ChatListener()).register();
    }

    public void registerCommands() {
        (new PitCommand()).register();
    }

    public HypixelAPI getHypixelAPI() {
        return this.hypixelAPI;
    }
}
