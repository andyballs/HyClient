package rage.pitclient;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.util.GuiElementTextField;
import rage.pitclient.util.RenderUtils;
import rage.pitclient.util.TextRenderUtils;

public class GuiWarningList extends GuiScreen {
	private JsonObject warningList;
	private static String currentGroup;
	private GuiElementTextField addGroupTf = new GuiElementTextField("", 0);
	private GuiElementTextField addPlayerTf = new GuiElementTextField("", 0);
	private GuiElementTextField colourTf = new GuiElementTextField("", 0);
	private GuiElementTextField tracerTf = new GuiElementTextField("", 0);

	private int xSize = 200;
	private int ySize = 300;

	private boolean showingDropdown = false;
	private boolean hasAtEdge = false;
	private int nameScroll = 0;

	private void addGroup(String groupName) {
		if (groupName.length() > 0 && !warningList.has(groupName)) {
			JsonObject group = new JsonObject();
			group.addProperty("colour", "c");
			group.addProperty("tracer", "c");
			group.add("players", (JsonElement) new JsonArray());
			warningList.add(groupName, (JsonElement) group);
			currentGroup = groupName;
		}
	}

	public GuiWarningList(JsonObject warningList) {
		this.warningList = warningList;
		if (warningList.entrySet().size() == 0) {
			addGroup("Default");
			currentGroup = "Default";
		}
		if (!warningList.has(currentGroup) && warningList.entrySet().size() > 0) {
			Iterator<Map.Entry<String, JsonElement>> iterator = warningList.entrySet().iterator();
			if (iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = iterator.next();
				currentGroup = entry.getKey();
			}

		}
	}

	private void addPlayer(String playerName) {
		if (playerName.length() > 0 && warningList.has(currentGroup)) {

			JsonArray arr = warningList.get(currentGroup).getAsJsonObject().get("players").getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				if (arr.get(i).getAsString().equalsIgnoreCase(playerName)) {
					return;
				}
			}
			arr.add((JsonElement) new JsonPrimitive(playerName));
		}
	}

	public void onGuiClosed() {
		super.onGuiClosed();
		Set<String> toRemove = new HashSet<>();
		for (Entry<String, JsonElement> entry : warningList.entrySet()) {
			if (entry.getValue().getAsJsonObject().get("players").getAsJsonArray().size() == 0) {
				toRemove.add(entry.getKey());
			}
		}
		for (String remove : toRemove) {
			warningList.remove(remove);
		}
		PitClient.getInstance().pitModConfigManager.savePlayerWarnings(warningList);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();

		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		int x = (scaledResolution.getScaledWidth() - xSize) / 2;
		int y = (scaledResolution.getScaledHeight() - ySize) / 2;

		RenderUtils.drawFloatingRect(x, y, xSize, ySize);

		FontRenderer fr = (Minecraft.getMinecraft()).fontRendererObj;

		for (int xOff = -2; xOff <= 2; xOff++) {
			for (int yOff = -2; yOff <= 2; yOff++) {
				if (xOff * xOff != yOff * yOff) {
					TextRenderUtils.drawStringCenteredScaledMaxWidth("PitMod Warning List", fr,
							(x + xSize / 2) + xOff / 2.0F, (y + 10) + yOff / 2.0F, false, 190, 1342177280);
				}
			}
		}

		TextRenderUtils.drawStringCenteredScaledMaxWidth(EnumChatFormatting.GOLD + "PitMod Warning List", fr,
				(x + xSize / 2), (y + 10), false, 190, -1);

		int addGroupLen = fr.getStringWidth("Add Group");
		int addGroupSize = xSize / 2 + addGroupLen + 7;
		Gui.drawRect(x + xSize - addGroupSize / 2 + 3, y + 25, x + xSize - addGroupSize / 2 + 6 + addGroupLen, y + 37,
				-7303024);

		fr.drawString("Add Group", (x + xSize - addGroupSize / 2 + 5), (y + 27), -14671840, false);
		addGroupTf.setSize(xSize / 2, 12);
		addGroupTf.render(x + xSize / 2 - addGroupSize / 2, y + 25);

		fr.drawString("Group: ", (x + 10), (y + 45), -12566464, false);
		int groupLen = fr.getStringWidth("Group: ");
		Gui.drawRect(x + 10 + groupLen + 5, y + 43, x + xSize - 10, y + 55, -16777216);
		TextRenderUtils.drawStringCenteredScaledMaxWidth(currentGroup, fr, x + (groupLen + 5 + xSize) / 2.0F, (y + 49),
				false, xSize - 25 - groupLen, -1);

		fr.drawString("Colour: ", (x + 10), (y + 65), -12566464, false);
		if (warningList.has(currentGroup)) {
			JsonObject warning = warningList.get(currentGroup).getAsJsonObject();
			colourTf.setText(warning.get("colour").getAsString());
		}
		colourTf.setSize(xSize - 25 - groupLen, 12);
		colourTf.render(x + 15 + groupLen, y + 63);

		fr.drawString("Tracer: ", (x + 10), (y + 85), -12566464, false);
		if (warningList.has(currentGroup)) {
			JsonObject warning = warningList.get(currentGroup).getAsJsonObject();
			tracerTf.setText(warning.get("tracer").getAsString());
		}
		tracerTf.setSize(xSize - 25 - groupLen, 12);
		tracerTf.render(x + 15 + groupLen, y + 83);

		int addPlayerLen = fr.getStringWidth("Add Player");
		int addPlayerSize = xSize / 2 + addPlayerLen + 7;
		Gui.drawRect(x + xSize - addPlayerSize / 2 + 3, y + 105, x + xSize - addPlayerSize / 2 + 6 + addPlayerLen,
				y + 117, -7303024);

		fr.drawString("Add Player", (x + xSize - addPlayerSize / 2 + 5), (y + 107), -14671840, false);
		addPlayerTf.setSize(xSize / 2, 12);
		addPlayerTf.render(x + xSize / 2 - addPlayerSize / 2, y + 105);

		for (int i = -2; i <= 2; i++) {
			for (int yOff = -2; yOff <= 2; yOff++) {
				if (i * i != yOff * yOff) {
					TextRenderUtils.drawStringCenteredScaledMaxWidth("Players", fr, (x + xSize / 2) + i / 2.0F,
							(y + 130) + yOff / 2.0F, false, 190, 1342177280);
				}
			}
		}

		TextRenderUtils.drawStringCenteredScaledMaxWidth(EnumChatFormatting.RED + "Players", fr, (x + xSize / 2),
				(y + 130), false, 190, -14671840);

		Gui.drawRect(x + 10, y + 140, x + xSize - 10, y + ySize - 10, -14513374);
		Gui.drawRect(x + 10 + 1, y + 140 + 1, x + xSize - 10 - 1, y + ySize - 10 - 1, -16777216);

		hasAtEdge = false;
		if (warningList.has(currentGroup)) {
			GL11.glEnable(3089);
			GL11.glScissor(0,
					(Minecraft.getMinecraft()).displayHeight - (y + ySize - 10 - 1) * scaledResolution.getScaleFactor(),
					(Minecraft.getMinecraft()).displayWidth, (ySize - 152) * scaledResolution.getScaleFactor());
			JsonArray arr = warningList.get(currentGroup).getAsJsonObject().get("players").getAsJsonArray();
			for (int j = 0; j < arr.size(); j++) {
				String name = arr.get(j).getAsString();
				fr.drawString(name, (x + 15), (y + 145 - nameScroll + 10 * j), -1, false);

				if (y + 165 - nameScroll + 10 * j + 10 > y + ySize - 10 - 1) {
					hasAtEdge = true;
				}
			}
			GL11.glDisable(3089);
		}

		if (showingDropdown) {
			int dropY = y + 55;
			int num = warningList.entrySet().size();
			if (warningList.has(currentGroup)) {
				num--;
			}
			int bottom = dropY + 12 * num;
			Gui.drawRect(x + 10 + groupLen + 5 + 2, dropY + 2, x + xSize - 10 + 2, bottom + 2, -2147483648);

			Gui.drawRect(x + 10 + groupLen + 5, dropY, x + xSize - 10, bottom, -7303024);

			int index = 0;
			for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>) warningList
					.entrySet()) {
				if (!((String) entry.getKey()).equals(currentGroup)) {
					Gui.drawRect(x + 10 + groupLen + 5 + 1, dropY + 1, x + xSize - 10 - 1,
							dropY + 12 + ((index == num - 1) ? -1 : 0), -16777216);

					TextRenderUtils.drawStringCenteredScaledMaxWidth(entry.getKey(), fr,
							x + (groupLen + 5 + xSize) / 2.0F, (dropY + 6), false, xSize - 25 - groupLen, -1);

					dropY += 12;
					index++;
				}
			}
		}
	}

	public void handleKeyboardInput() throws IOException {
		Keyboard.enableRepeatEvents(true);
		super.handleKeyboardInput();
		if (Keyboard.getEventKeyState()) {
			if (addGroupTf.getFocus()) {
				addGroupTf.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
			} else if (addPlayerTf.getFocus()) {
				addPlayerTf.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
			} else if (colourTf.getFocus()) {
				colourTf.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
				if (warningList.has(currentGroup)) {
					warningList.get(currentGroup).getAsJsonObject().addProperty("colour", colourTf.getText());
				}
			} else if (tracerTf.getFocus()) {
				tracerTf.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
				if (warningList.has(currentGroup)) {
					warningList.get(currentGroup).getAsJsonObject().addProperty("tracer", tracerTf.getText());
				}
			}
		}
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		int x = (scaledResolution.getScaledWidth() - xSize) / 2;
		int y = (scaledResolution.getScaledHeight() - ySize) / 2;
		FontRenderer fr = (Minecraft.getMinecraft()).fontRendererObj;

		int scaledWidth = scaledResolution.getScaledWidth();
		int scaledHeight = scaledResolution.getScaledHeight();
		int mouseX = Mouse.getX() * scaledWidth / (Minecraft.getMinecraft()).displayWidth;
		int mouseY = scaledHeight - Mouse.getY() * scaledHeight / (Minecraft.getMinecraft()).displayHeight - 1;

		if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
			return;
		}

		if (Mouse.getEventDWheel() != 0) {
			int d = Mouse.getEventDWheel();
			if (d < 0 || hasAtEdge) {
				nameScroll += d / 60;
			}
			if (nameScroll < 0) {
				nameScroll = 0;
			}
		}
		if (Mouse.getEventButtonState() && (Mouse.getEventButton() == 0 || Mouse.getEventButton() == 1)) {
			int groupLen = fr.getStringWidth("Group: ");
			if (showingDropdown) {
				showingDropdown = false;
				int dropY = y + 55;
				for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>) warningList
						.entrySet()) {
					if (!((String) entry.getKey()).equals(currentGroup)) {
						if (mouseX > x + 10 + groupLen + 5 && mouseX < x + xSize - 10 && mouseY > dropY
								&& mouseY < dropY + 12) {

							nameScroll = 0;
							currentGroup = entry.getKey();
							return;
						}
						dropY += 12;
					}
				}
				return;
			}
			addGroupTf.unfocus();
			addPlayerTf.unfocus();
			colourTf.unfocus();
			tracerTf.unfocus();

			int addGroupLen = fr.getStringWidth("Add Group");
			int addGroupSize = xSize / 2 + addGroupLen + 7;
			if (mouseY >= y + 25 && mouseY <= y + 37) {
				if (mouseX < x + xSize - addGroupSize / 2 + 3) {
					addGroupTf.mouseClicked(mouseX, mouseY, Mouse.getEventButton());
				} else if (mouseX <= x + xSize - addGroupSize / 2 + 6 + addGroupLen) {
					addGroup(addGroupTf.getText());
				}
			}

			if (mouseY > y + 120 && mouseY < y + ySize - 10 && warningList.has(currentGroup)) {
				JsonArray arr = warningList.get(currentGroup).getAsJsonObject().get("players").getAsJsonArray();
				Iterator<JsonElement> iterator = arr.iterator();
				int i = 0;
				while (iterator.hasNext()) {
					iterator.next();
					if (mouseY > y + 145 - nameScroll + 10 * i && mouseY < y + 145 - nameScroll + 10 * i + 10) {
						iterator.remove();
						return;
					}
					i++;
				}
			}

			if (mouseX > x + 10 + groupLen + 5 && mouseX < x + xSize - 10 && mouseY > y + 43 && mouseY < y + 55) {
				showingDropdown = true;
			}

			if (mouseX > x + 15 + groupLen && mouseX < x + xSize - 10 && mouseY > y + 63 && mouseY < y + 75) {
				colourTf.mouseClicked(mouseX, mouseY, Mouse.getEventButton());
			}

			if (mouseX > x + 15 + groupLen && mouseX < x + xSize - 10 && mouseY > y + 83 && mouseY < y + 95) {
				tracerTf.mouseClicked(mouseX, mouseY, Mouse.getEventButton());
			}

			int addPlayerLen = fr.getStringWidth("Add Player");
			int addPlayerSize = xSize / 2 + addPlayerLen + 7;
			if (mouseY >= y + 105 && mouseY <= y + 117)
				if (mouseX < x + xSize - addPlayerSize / 2 + 3) {
					addPlayerTf.mouseClicked(mouseX, mouseY, Mouse.getEventButton());
				} else if (mouseX <= x + xSize - addPlayerSize / 2 + 6 + addPlayerLen) {
					addPlayer(addPlayerTf.getText());
				}
		}
	}
}