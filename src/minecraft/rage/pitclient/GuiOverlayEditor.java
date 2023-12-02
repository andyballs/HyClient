package rage.pitclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.text.WordUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import rage.pitclient.util.RenderUtils;

public class GuiOverlayEditor extends GuiScreen {
	private static final ResourceLocation button_white = new ResourceLocation("pitmod/button_white.png");
	private static final ResourceLocation off = new ResourceLocation("pitmod/off.png");
	private static final ResourceLocation on = new ResourceLocation("pitmod/on.png");
	private static final ResourceLocation slider_button = new ResourceLocation("pitmod/slider_button.png");
	private static final ResourceLocation slider_off = new ResourceLocation("pitmod/slider_off.png");
	private static final ResourceLocation slider_on = new ResourceLocation("pitmod/slider_on.png");

	private final JsonObject overlays;

	private boolean editingOverlaySettings = false;
	private String clickedOverlay;
	private int clickedXOffset;
	private int clickedYOffset;
	private int clickedX;
	private int clickedY;
	private HashMap<String, GuiTextField> textFields = new HashMap<>();

	public GuiOverlayEditor(JsonObject overlays) {
		this.overlays = overlays;
	}

	public String getDisplayForId(String overlayId) {
		switch (overlayId) {
		case "telebow":
			return overlays.get("telebow").getAsJsonObject().get("custom_text").getAsString() + "20.0s";
		case "aop":
			return overlays.get("aop").getAsJsonObject().get("custom_text").getAsString() + "15.0s";
		case "playerList":
			return overlays.get("playerList").getAsJsonObject().get("custom_text").getAsString();
		}
		return "Unknown Overlay!";
	}

	public int getWidthForId(String overlayId) {
		if (overlayId.equals("playerList")) {
			return 100;
		}
		FontRenderer fr = (Minecraft.getMinecraft()).fontRendererObj;
		return 5 + fr.getStringWidth(getDisplayForId(overlayId));
	}

	public int getHeightForId(String overlayId) {
		if (overlayId.equals("playerList")) {
			return 50;
		}
		return 13;
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1) {
			if (editingOverlaySettings) {
				editingOverlaySettings = false;
				return;
			}
			PitClient.getInstance().pitModConfigManager.saveOverlays(overlays);
		} else if (editingOverlaySettings && clickedOverlay != null && overlays.has(clickedOverlay)) {
			for (Map.Entry<String, GuiTextField> entry : textFields.entrySet()) {
				if (((GuiTextField) entry.getValue()).textboxKeyTyped(typedChar, keyCode)) {
					overlays.get(clickedOverlay).getAsJsonObject().addProperty(entry.getKey(),
							((GuiTextField) entry.getValue()).getText());

					return;
				}
			}
		}
		super.keyTyped(typedChar, keyCode);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>) overlays.entrySet()) {
			JsonObject overlay = ((JsonElement) entry.getValue()).getAsJsonObject();
			if (!overlay.has("x"))
				overlay.addProperty("x", Integer.valueOf(0));
			if (!overlay.has("y"))
				overlay.addProperty("y", Integer.valueOf(0));

			int x = (int) (overlay.get("x").getAsFloat() * scaledResolution.getScaledWidth());
			int y = (int) (overlay.get("y").getAsFloat() * scaledResolution.getScaledHeight());
			int width = getWidthForId(entry.getKey());
			int height = getHeightForId(entry.getKey());

			Gui.drawRect(x, y, x + width, y + height, -265277392);
			(Minecraft.getMinecraft()).fontRendererObj.drawString(getDisplayForId(entry.getKey()), x + 3, y + 3, -1);
		}
		if (editingOverlaySettings && clickedOverlay != null && overlays.has(clickedOverlay)) {
			JsonObject overlay = overlays.get(clickedOverlay).getAsJsonObject();

			int width = 120;
			int height = 5;
			for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>) overlay.entrySet()) {
				if (((String) entry.getKey()).equalsIgnoreCase("x")
						|| ((String) entry.getKey()).equalsIgnoreCase("y")) {
					continue;
				}
				height += 28;
			}
			int settingOverlayX = Math.min(clickedX, scaledResolution.getScaledWidth() - width - 5);
			RenderUtils.drawFloatingRect(settingOverlayX, clickedY, width, height);
			height = 5;
			for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>) overlay.entrySet()) {
				if (((String) entry.getKey()).equalsIgnoreCase("x")
						|| ((String) entry.getKey()).equalsIgnoreCase("y")) {
					continue;
				}
				String name = WordUtils.capitalizeFully(((String) entry.getKey()).replace("_", " "));
				int nameLen = fontRendererObj.getStringWidth(name);
				fontRendererObj.drawString(name, settingOverlayX + width / 2 - nameLen / 2, clickedY + height,
						-14540254);
				if (((JsonElement) entry.getValue()).isJsonPrimitive()
						&& ((JsonElement) entry.getValue()).getAsJsonPrimitive().isBoolean()) {
					if (((JsonElement) entry.getValue()).getAsBoolean()) {
						Minecraft.getMinecraft().getTextureManager().bindTexture(on);
					} else {
						Minecraft.getMinecraft().getTextureManager().bindTexture(off);
					}
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					RenderUtils.drawTexturedRect((settingOverlayX + width / 2 - 24), (clickedY + height + 10), 48.0F,
							16.0F);
				} else if (((JsonElement) entry.getValue()).isJsonPrimitive()
						&& ((JsonElement) entry.getValue()).getAsJsonPrimitive().isString()) {
					if (!textFields.containsKey(entry.getKey())) {
						textFields.put(entry.getKey(), new GuiTextField(0, fontRendererObj, 0, 0, width - 10, 16));
					}

					GuiTextField tf = textFields.get(entry.getKey());
					tf.xPosition = settingOverlayX + 5;
					tf.yPosition = clickedY + height + 10;
					tf.setText(((JsonElement) entry.getValue()).getAsString());
					tf.drawTextBox();
				}
				height += 28;
			}
		}
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		if (editingOverlaySettings && clickedOverlay != null && overlays.has(clickedOverlay)) {
			JsonObject overlay = overlays.get(clickedOverlay).getAsJsonObject();

			int width = 120;
			int height = 5;
			int settingOverlayX = Math.min(clickedX, scaledResolution.getScaledWidth() - width - 5);

			for (Entry<String, JsonElement> entry : overlay.entrySet()) {
				if (((String) entry.getKey()).equalsIgnoreCase("x")
						|| ((String) entry.getKey()).equalsIgnoreCase("y")) {
					continue;
				}
				if (mouseX > settingOverlayX && mouseX < settingOverlayX + width && mouseY > clickedY + height
						&& mouseY < clickedY + height + 28) {
					if (((JsonElement) entry.getValue()).isJsonPrimitive()
							&& ((JsonElement) entry.getValue()).getAsJsonPrimitive().isBoolean()) {
						entry.setValue(
								new JsonPrimitive(Boolean.valueOf(!((JsonElement) entry.getValue()).getAsBoolean())));
					} else if (((JsonElement) entry.getValue()).isJsonPrimitive()
							&& ((JsonElement) entry.getValue()).getAsJsonPrimitive().isString()) {
						if (!textFields.containsKey(entry.getKey())) {
							textFields.put(entry.getKey(), new GuiTextField(0, fontRendererObj, 0, 0, width - 10, 16));
						}

						GuiTextField tf = textFields.get(entry.getKey());
						for (GuiTextField otherTf : textFields.values()) {
							otherTf.setFocused((otherTf == tf));
						}
					}

					return;
				}

				height += 28;
			}
			clickedOverlay = null;
		}
		for (Entry<String, JsonElement> entry : overlays.entrySet()) {
			JsonObject overlay = ((JsonElement) entry.getValue()).getAsJsonObject();
			if (!overlay.has("x"))
				overlay.addProperty("x", Integer.valueOf(0));
			if (!overlay.has("y"))
				overlay.addProperty("y", Integer.valueOf(0));

			int x = (int) (overlay.get("x").getAsFloat() * scaledResolution.getScaledWidth());
			int y = (int) (overlay.get("y").getAsFloat() * scaledResolution.getScaledHeight());
			int width = getWidthForId(entry.getKey());
			int height = getHeightForId(entry.getKey());
			
			if (mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height) {
				clickedOverlay = entry.getKey();
				clickedXOffset = x - mouseX;
				clickedYOffset = y - mouseY;
				clickedX = mouseX;
				clickedY = mouseY;
				editingOverlaySettings = (mouseButton != 0);
				textFields.clear();

				return;
			}
		}
		clickedOverlay = null;
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		if (!editingOverlaySettings && clickedOverlay != null && overlays.has(clickedOverlay)) {
			JsonObject overlay = overlays.get(clickedOverlay).getAsJsonObject();
			
			if (!overlay.has("x")) overlay.addProperty("x", Double.valueOf(0.0D));
			if (!overlay.has("y")) overlay.addProperty("y", Double.valueOf(0.0D));
			
			overlay.addProperty("x", ((double) clickedXOffset + mouseX)/((double)scaledResolution.getScaledWidth()));
			overlay.addProperty("y", ((double) clickedYOffset + mouseY)/((double)scaledResolution.getScaledHeight()));
		}
		
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
}
