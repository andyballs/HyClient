package rage.pitclient.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class PitModConfigManager {
	
	public PitModConfigManager() {
		configDir = new File(System.getProperty("user.home")+"\\AppData\\Roaming\\.rageclient");
		new File(System.getProperty("user.home")+"\\AppData\\Roaming\\.rageclient\\configs").mkdir();
		configDir.mkdir();
	}
	
	public File configDir;
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private JsonObject playerWarnings;

	private JsonObject events;
	private JsonObject config;
	private JsonObject overlays;
	
	public JsonObject getPlayerWarnings() {
		if (playerWarnings != null) {
			return playerWarnings;
		}

		File file = new File(configDir, "playerWarnings2.json");
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			playerWarnings = (JsonObject) gson.fromJson(reader, JsonObject.class);
		} catch (Exception exception) {
		}
		if (playerWarnings == null) {
			playerWarnings = new JsonObject();
		}
		return playerWarnings;
	}

	public void savePlayerWarnings(JsonObject warning) {
		playerWarnings = warning;
		File file = new File(configDir, "playerWarnings2.json");
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();

			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
				writer.write(gson.toJson((JsonElement) warning));
			}
		} catch (IOException iOException) {
		}
	}

	public JsonObject getConfig() {
		if (config != null) {
			return fillConfig(config);
		}

		File file = new File(configDir, "config.json");
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			config = (JsonObject) gson.fromJson(reader, JsonObject.class);
		} catch (Exception exception) {
		}
		config = fillConfig(config);
		return config;
	}

	public JsonObject fillConfig(JsonObject config) {
		if (config == null)
			config = new JsonObject();
		if (!config.has("apikey"))
			config.addProperty("apikey", "");
		if (!config.has("alert_sfx"))
			config.addProperty("alert_sfx", Boolean.valueOf(true));
		if (!config.has("alert_center"))
			config.addProperty("alert_center", Boolean.valueOf(true));
		if (!config.has("alert_nicks"))
			config.addProperty("alert_nicks", Boolean.valueOf(true));
		return config;
	}

	public void saveConfig() {
		File file = new File(configDir, "config.json");
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();

			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
				writer.write(gson.toJson((JsonElement) config));
			}
		} catch (IOException iOException) {
		}
	}

	public JsonObject getEvents() {
		if (events != null) {
			return events;
		}

		File file = new File(configDir, "events.json");
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			events = (JsonObject) gson.fromJson(reader, JsonObject.class);
		} catch (Exception exception) {
		}
		if (events == null) {
			events = new JsonObject();
		}
		return events;
	}

	public void clearEvents() {
		events = new JsonObject();
		saveEvents();
	}

	public void saveEvents() {
		File file = new File(configDir, "events.json");
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();

			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
				writer.write(gson.toJson((JsonElement) events));
			}
		} catch (IOException iOException) {
		}
	}

	public JsonObject getOverlays() {
		if (overlays != null) {
			return fillDefaultOverlays(overlays);
		}

		File file = new File(configDir, "overlays.json");
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			overlays = (JsonObject) gson.fromJson(reader, JsonObject.class);
		} catch (Exception exception) {
		}
		overlays = fillDefaultOverlays(overlays);
		return overlays;
	}

	public void saveOverlays(JsonObject overlay) {
		overlays = overlay;
		File file = new File(configDir, "overlays.json");
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();

			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
				writer.write(gson.toJson((JsonElement) overlay));
			}
		} catch (IOException iOException) {
		}
	}

	private JsonObject fillDefaultOverlays(JsonObject overlays) {
		JsonObject telebowTimer, aopTimer, playerList;
		if (overlays == null)
			overlays = new JsonObject();

		if (overlays.has("telebow")) {
			telebowTimer = overlays.get("telebow").getAsJsonObject();
		} else {
			telebowTimer = new JsonObject();
		}
		fillDefaultTelebowOverlay(telebowTimer);

		if (overlays.has("aop")) {
			aopTimer = overlays.get("aop").getAsJsonObject();
		} else {
			aopTimer = new JsonObject();
		}
		fillDefaultAOPOverlay(aopTimer);

		if (overlays.has("playerList")) {
			playerList = overlays.get("playerList").getAsJsonObject();
		} else {
			playerList = new JsonObject();
		}
		fillDefaultPlayerListOverlay(playerList);

		overlays.add("telebow", (JsonElement) telebowTimer);
		overlays.add("aop", (JsonElement) aopTimer);
		overlays.add("playerList", (JsonElement) playerList);
		return overlays;
	}

	private void fillDefaultTelebowOverlay(JsonObject object) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		if (!object.has("x"))
			object.addProperty("x", Double.valueOf(5.0D / scaledResolution.getScaledWidth_double()));
		if (!object.has("y"))
			object.addProperty("y", Double.valueOf(5.0D / scaledResolution.getScaledHeight_double()));
		if (!object.has("disable"))
			object.addProperty("disable", Boolean.valueOf(false));
		if (!object.has("show_background"))
			object.addProperty("show_background", Boolean.valueOf(false));
		if (!object.has("text_shadow"))
			object.addProperty("text_shadow", Boolean.valueOf(true));
		if (!object.has("custom_text"))
			object.addProperty("custom_text", "Telebow Cooldown: ");
		if (!object.has("text_colour"))
			object.addProperty("text_colour", "FFFFDF00");
		if (object.get("text_colour").getAsString().startsWith("colour:")
				|| object.get("text_colour").getAsString().startsWith("hexcolour:")) {
			object.addProperty("text_colour", "FFFFDF00");
		}
	}

	private void fillDefaultPlayerListOverlay(JsonObject object) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		if (!object.has("x"))
			object.addProperty("x", Double.valueOf(50.0D / scaledResolution.getScaledWidth_double()));
		if (!object.has("y"))
			object.addProperty("y", Double.valueOf(5.0D / scaledResolution.getScaledHeight_double()));
		if (!object.has("disable"))
			object.addProperty("disable", Boolean.valueOf(false));
		if (!object.has("show_background"))
			object.addProperty("show_background", Boolean.valueOf(false));
		if (!object.has("text_shadow"))
			object.addProperty("text_shadow", Boolean.valueOf(true));
		if (!object.has("custom_text"))
			object.addProperty("custom_text", "Player List");
		if (!object.has("text_colour"))
			object.addProperty("text_colour", "FFFFDF00");
		if (object.get("text_colour").getAsString().startsWith("colour:")
				|| object.get("text_colour").getAsString().startsWith("hexcolour:")) {
			object.addProperty("text_colour", "FFFFDF00");
		}
	}

	private void fillDefaultAOPOverlay(JsonObject object) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		if (!object.has("x"))
			object.addProperty("x", Double.valueOf(5.0D / scaledResolution.getScaledWidth_double()));
		if (!object.has("y"))
			object.addProperty("y", Double.valueOf(20.0D / scaledResolution.getScaledHeight_double()));
		if (!object.has("disable"))
			object.addProperty("disable", Boolean.valueOf(false));
		if (!object.has("show_background"))
			object.addProperty("show_background", Boolean.valueOf(false));
		if (!object.has("text_shadow"))
			object.addProperty("text_shadow", Boolean.valueOf(true));
		if (!object.has("custom_text"))
			object.addProperty("custom_text", "AoP Cooldown: ");
		if (!object.has("text_colour"))
			object.addProperty("text_colour", "hexcolour:FFFFDF00");
		if (object.get("text_colour").getAsString().startsWith("colour:")
				|| object.get("text_colour").getAsString().startsWith("hexcolour:")) {
			object.addProperty("text_colour", "FFFFDF00");
		}
	}

}
