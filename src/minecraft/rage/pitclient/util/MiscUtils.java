package rage.pitclient.util;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class MiscUtils {
	
	public static float getElementAsFloat(JsonElement element, float def) {
		if (element == null)
			return def;
		if (!element.isJsonPrimitive())
			return def;
		JsonPrimitive prim = element.getAsJsonPrimitive();
		if (!prim.isNumber())
			return def;
		return prim.getAsFloat();
	}

	public static String getElementAsString(JsonElement element, String def) {
		if (element == null)
			return def;
		if (!element.isJsonPrimitive())
			return def;
		JsonPrimitive prim = element.getAsJsonPrimitive();
		if (!prim.isString())
			return def;
		return prim.getAsString();
	}

	public static Splitter PATH_SPLITTER = Splitter.on(".").omitEmptyStrings().limit(2);

	public static JsonElement getElement(JsonElement element, String path) {
		List<String> path_split = PATH_SPLITTER.splitToList(path);
		if (element instanceof com.google.gson.JsonObject) {
			JsonElement e = element.getAsJsonObject().get(path_split.get(0));
			if (path_split.size() > 1) {
				return getElement(e, path_split.get(1));
			}
			return e;
		}

		return element;
	}

	public static void copyToClipboard(String str) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(str), null);
	}

	public static void setCursor(ResourceLocation loc, int hotspotX, int hotspotY) {
		try {
			BufferedImage image = ImageIO
					.read(Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream());
			int maxSize = Cursor.getMaxCursorSize();
			IntBuffer buffer = BufferUtils.createIntBuffer(maxSize * maxSize);
			for (int i = 0; i < maxSize * maxSize; i++) {
				int cursorX = i % maxSize;
				int cursorY = i / maxSize;
				if (cursorX >= image.getWidth() || cursorY >= image.getHeight()) {
					buffer.put(0);
				} else {
					buffer.put(image.getRGB(cursorX, image.getHeight() - 1 - cursorY));
				}
			}
			buffer.flip();
			Mouse.setNativeCursor(new Cursor(maxSize, maxSize, hotspotX, hotspotY, 1, buffer, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> getSidebarScores(Scoreboard scoreboard) {
		List<String> found = new ArrayList<>();

		ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
		if (sidebar != null) {
			List<Score> scores = new ArrayList<>(scoreboard.getScores());
			/*
			 * Scores retrieved here do not care for ordering, this is done by the
			 * Scoreboard its self. We'll need to do this our selves in this case.
			 * 
			 * This will appear backwars in chat, but remember that the scoreboard reverses
			 * this order to ensure highest scores go first.
			 */
			scores.sort(Comparator.comparingInt(Score::getScorePoints));

			found = scores.stream().filter(score -> score.getObjective().getName().equals(sidebar.getName())).map(
					score -> score.getPlayerName() + getSuffixFromContainingTeam(scoreboard, score.getPlayerName()))
					.collect(Collectors.toList());

		}
		return found;
	}

	private static String getSuffixFromContainingTeam(Scoreboard scoreboard, String member) {
		String suffix = null;
		for (ScorePlayerTeam team : scoreboard.getTeams()) {
			if (team.getMembershipCollection().contains(member)) {
				suffix = team.getColorSuffix();
				break;
			}
		}
		return (suffix == null ? "" : suffix);
	}

	private static final String USERNAME_API_URL = "https://api.mojang.com/user/profiles/%s/names";

	public static String getNameFromUUID(String uuid) {
		try (BufferedReader reader = Resources
				.asCharSource(new URL(String.format(USERNAME_API_URL, uuid)), StandardCharsets.UTF_8)
				.openBufferedStream()) {
			JsonReader json = new JsonReader(reader);
			json.beginArray();

			String name = null;
			long when = 0;

			while (json.hasNext()) {
				String nameObj = null;
				long timeObj = 0;
				json.beginObject();
				while (json.hasNext()) {
					String key = json.nextName();
					switch (key) {
					case "name":
						nameObj = json.nextString();
						break;
					case "changedToAt":
						timeObj = json.nextLong();
						break;
					default:
						json.skipValue();
						break;
					}
				}
				json.endObject();

				if (nameObj != null && timeObj >= when) {
					name = nameObj;
				}
			}

			json.endArray();
			json.close();
			if (name == null) {
				throw new IOException("Failed connecting to the Mojang API");
			}
			return name;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Invalid";

	}
}
