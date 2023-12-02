package rage.pitclient.clickgui.util;

import java.awt.Font;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StringUtils;
import rage.pitclient.util.UnicodeFontRenderer;

public class FontUtil {
	private static FontRenderer fontRenderer;

	private static HashMap<String,FontRenderer> fontRendererMap = new HashMap<>();
	
	public static void setupFontUtils() {
		int fontSize = 15;
		fontRenderer = new UnicodeFontRenderer(new Font("Trebuchet MS", Font.PLAIN, 15));
		fontRendererMap.put("Minecraft", Minecraft.getMinecraft().fontRendererObj);
		fontRendererMap.put("Trebuchet MS", new UnicodeFontRenderer(new Font("Trebuchet MS", Font.PLAIN, fontSize)));
		fontRendererMap.put("Trebuchet MS Bold", new UnicodeFontRenderer(new Font("Trebuchet MS", Font.BOLD, fontSize)));
		fontRendererMap.put("Helvetica", new UnicodeFontRenderer(new Font("Helvetica", Font.BOLD, fontSize)));
		fontRendererMap.put("Calibri Bold", new UnicodeFontRenderer(new Font("Calibri", Font.BOLD, fontSize)));
	}
	
	public static FontRenderer getFontRenderer() {
		return fontRenderer;
	}
	
	public static HashMap<String,FontRenderer> getFontRendererMap() {
		return fontRendererMap;
	}
	
	public static FontRenderer getFontRendererByName(String name) {
		FontRenderer font = null;
		for (String set : fontRendererMap.keySet()) {
			if (name.equalsIgnoreCase(set)) font = fontRendererMap.get(set);
		}
		return font;
	}

	public static int getStringWidth(String text) {
		return fontRenderer.getStringWidth(StringUtils.stripControlCodes(text));
	}

	public static int getFontHeight() {
		return fontRenderer.FONT_HEIGHT;
	}

	public static void drawString(String text, double x, double y, int color) {
		fontRenderer.drawString(text, (int) x, (int) y, color);
	}

	public static void drawStringWithShadow(String text, double x, double y, int color) {
		fontRenderer.drawStringWithShadow(text, (float) x, (float) y, color);
	}

	public static void drawCenteredString(String text, double x, double y, int color) {
		drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
	}

	public static void drawCenteredStringWithShadow(String text, double x, double y, int color) {
		drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y, color);
	}

	public static void drawTotalCenteredString(String text, double x, double y, int color) {
		drawString(text, x - fontRenderer.getStringWidth(text) / 2, y - fontRenderer.FONT_HEIGHT / 2, color);
	}

	public static void drawTotalCenteredStringWithShadow(String text, double x, double y, int color) {
		drawStringWithShadow(text, x - fontRenderer.getStringWidth(text) / 2, y - fontRenderer.FONT_HEIGHT / 2F, color);
	}
}
