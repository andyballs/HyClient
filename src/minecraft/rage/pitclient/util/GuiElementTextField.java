package rage.pitclient.util;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class GuiElementTextField {
	public static final int SCALE_TEXT = 32;
	public static final int NUM_ONLY = 16;
	public static final int NO_SPACE = 8;
	public static final int FORCE_CAPS = 4;
	public static final int COLOUR = 2;
	public static final int MULTILINE = 1;
	private int searchBarYSize;
	private int searchBarXSize;
	private static final int searchBarPadding = 2;
	private int options;
	private boolean focus = false;
	private int x;
	private int y;
	private String prependText = "";

	private final GuiTextField textField = new GuiTextField(0, (Minecraft.getMinecraft()).fontRendererObj, 0, 0, 0, 0);

	private int customBorderColour = -1;

	public GuiElementTextField(String initialText, int options) {
		this(initialText, 100, 20, options);
	}

	public GuiElementTextField(String initialText, int sizeX, int sizeY, int options) {
		this.textField.setFocused(true);
		this.textField.setCanLoseFocus(false);
		this.textField.setMaxStringLength(999);
		this.textField.setText(initialText);
		this.searchBarXSize = sizeX;
		this.searchBarYSize = sizeY;
		this.options = options;
	}

	public void setMaxStringLength(int len) {
		this.textField.setMaxStringLength(len);
	}

	public void setCustomBorderColour(int colour) {
		this.customBorderColour = colour;
	}

	public String getText() {
		return this.textField.getText();
	}

	public void setPrependText(String text) {
		this.prependText = text;
	}

	public void setText(String text) {
		if (this.textField.getText() == null || !this.textField.getText().equals(text)) {
			this.textField.setText(text);
		}
	}

	public void setSize(int searchBarXSize, int searchBarYSize) {
		this.searchBarXSize = searchBarXSize;
		this.searchBarYSize = searchBarYSize;
	}

	public String toString() {
		return this.textField.getText();
	}

	public boolean getFocus() {
		return this.focus;
	}

	public int getHeight() {
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int paddingUnscaled = 2 / scaledresolution.getScaleFactor();

		int numLines = StringUtils.countMatches(this.textField.getText(), "\n") + 1;
		int extraSize = (this.searchBarYSize - 8) / 2 + 8;
		int bottomTextBox = this.searchBarYSize + extraSize * (numLines - 1);

		return bottomTextBox + paddingUnscaled * 2;
	}

	public int getWidth() {
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		int paddingUnscaled = 2 / scaledresolution.getScaleFactor();

		return this.searchBarXSize + paddingUnscaled * 2;
	}

	private float getScaleFactor(String str) {
		return Math.min(1.0F,
				(this.searchBarXSize - 2) / (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(str));
	}

	private boolean isScaling() {
		return ((this.options & 0x20) != 0);
	}

	private float getStringWidth(String str) {
		if (isScaling()) {
			return (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(str) * getScaleFactor(str);
		}
		return (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(str);
	}

	public int getCursorPos(int mouseX, int mouseY) {
		int xComp = mouseX - this.x;
		int yComp = mouseY - this.y;

		int extraSize = (this.searchBarYSize - 8) / 2 + 8;

		String renderText = this.prependText + this.textField.getText();

		int lineNum = Math.round(((yComp - (this.searchBarYSize - 8) / 2) / extraSize));

		Pattern patternControlCode = Pattern.compile("(?i)\\u00A7([^\\u00B6])(?!\\u00B6)");
		String text = renderText;
		String textNoColour = renderText;
		if ((this.options & 0x2) != 0) {
			while (true) {
				Matcher matcher = patternControlCode.matcher(text);
				if (!matcher.find() || matcher.groupCount() < 1)
					break;
				String code = matcher.group(1);
				
				text = matcher.replaceFirst("\u00A7" +code + "\\u00B6" + code);
			}
		}
		while (true) {
			Matcher matcher = patternControlCode.matcher(textNoColour);
			if (!matcher.find() || matcher.groupCount() < 1)
				break;
			String code = matcher.group(1);
			textNoColour = matcher.replaceFirst("\\u00B6" + code);
		}

		int currentLine = 0;
		int cursorIndex = 0;
		for (; cursorIndex < textNoColour.length() && currentLine != lineNum; cursorIndex++) {
			if (textNoColour.charAt(cursorIndex) == '\n') {
				currentLine++;
			}
		}

		String textNC = textNoColour.substring(0, cursorIndex);
		int colorCodes = StringUtils.countMatches(textNC, "\\u00B6");
		String line = text.substring(cursorIndex + (((this.options & 0x2) != 0) ? (colorCodes * 2) : 0)).split("\n")[0];
		int padding = Math.min(5, this.searchBarXSize - strLenNoColor(line)) / 2;
		String trimmed = (Minecraft.getMinecraft()).fontRendererObj.trimStringToWidth(line, xComp - padding);
		int linePos = strLenNoColor(trimmed);
		if (linePos != strLenNoColor(line)) {
			char after = line.charAt(linePos);
			int trimmedWidth = (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(trimmed);
			int charWidth = (Minecraft.getMinecraft()).fontRendererObj.getCharWidth(after);
			if (trimmedWidth + charWidth / 2 < xComp - padding) {
				linePos++;
			}
		}
		cursorIndex += linePos;

		int pre = rage.pitclient.util.StringUtils.cleanColour(prependText).length();
		if (cursorIndex < pre) {
			cursorIndex = 0;
		} else {
			cursorIndex -= pre;
		}

		return cursorIndex;
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 1) {
			this.textField.setText("");
		} else {
			this.textField.setCursorPosition(getCursorPos(mouseX, mouseY));
		}
		this.focus = true;
	}

	public void unfocus() {
		this.focus = false;
		this.textField.setSelectionPos(this.textField.getCursorPosition());
	}

	public int strLenNoColor(String str) {
		return str.replaceAll("(?i)\\u00A7.", "").length();
	}

	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		if (this.focus) {
			this.textField.setSelectionPos(getCursorPos(mouseX, mouseY));
		}
	}

	public void keyTyped(char typedChar, int keyCode) {
		if (this.focus) {
			if ((this.options & 0x1) != 0) {
				Pattern patternControlCode = Pattern.compile("(?i)\\u00A7([^\\u00B6\n])(?!\\u00B6)");

				String text = this.textField.getText();
				String textNoColour = this.textField.getText();
				while (true) {
					Matcher matcher = patternControlCode.matcher(text);
					if (!matcher.find() || matcher.groupCount() < 1)
						break;
					String code = matcher.group(1);
					text = matcher.replaceFirst("\u00A7" + code + "\\u00B6" + code);
				}
				while (true) {
					Matcher matcher = patternControlCode.matcher(textNoColour);
					if (!matcher.find() || matcher.groupCount() < 1)
						break;
					String code = matcher.group(1);
					textNoColour = matcher.replaceFirst("\\u00B6" + code);
				}

				if (keyCode == 28) {
					String before = this.textField.getText().substring(0, this.textField.getCursorPosition());
					String after = this.textField.getText().substring(this.textField.getCursorPosition());
					int pos = this.textField.getCursorPosition();
					this.textField.setText(before + "\n" + after);
					this.textField.setCursorPosition(pos + 1);
					return;
				}
				if (keyCode == 200) {
					int textBeforeCursorWidth;
					String lineBefore, thisLineBeforeCursor,
							textNCBeforeCursor = textNoColour.substring(0, this.textField.getSelectionEnd());
					int colorCodes = StringUtils.countMatches(textNCBeforeCursor, "\\u00B6");
					String textBeforeCursor = text.substring(0, this.textField.getSelectionEnd() + colorCodes * 2);

					int numLinesBeforeCursor = StringUtils.countMatches(textBeforeCursor, "\n");

					String[] split = textBeforeCursor.split("\n");

					if (split.length == numLinesBeforeCursor && split.length > 0) {
						textBeforeCursorWidth = 0;
						lineBefore = split[split.length - 1];
						thisLineBeforeCursor = "";
					} else if (split.length > 1) {
						thisLineBeforeCursor = split[split.length - 1];
						lineBefore = split[split.length - 2];
						textBeforeCursorWidth = (Minecraft.getMinecraft()).fontRendererObj
								.getStringWidth(thisLineBeforeCursor);
					} else {
						return;
					}

					String trimmed = (Minecraft.getMinecraft()).fontRendererObj.trimStringToWidth(lineBefore,
							textBeforeCursorWidth);
					int linePos = strLenNoColor(trimmed);
					if (linePos != strLenNoColor(lineBefore)) {
						char after = lineBefore.charAt(linePos);
						int trimmedWidth = (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(trimmed);
						int charWidth = (Minecraft.getMinecraft()).fontRendererObj.getCharWidth(after);
						if (trimmedWidth + charWidth / 2 < textBeforeCursorWidth) {
							linePos++;
						}
					}

					int newPos = this.textField.getSelectionEnd() - strLenNoColor(thisLineBeforeCursor)
							- strLenNoColor(lineBefore) - 1 + linePos;

					if (GuiScreen.isShiftKeyDown()) {
						this.textField.setSelectionPos(newPos);
					} else {
						this.textField.setCursorPosition(newPos);
					}
				} else if (keyCode == 208) {
					String thisLineBeforeCursor;
					int textBeforeCursorWidth;
					String textNCBeforeCursor = textNoColour.substring(0, this.textField.getSelectionEnd());
					int colorCodes = StringUtils.countMatches(textNCBeforeCursor, "\\u00B6");
					String textBeforeCursor = text.substring(0, this.textField.getSelectionEnd() + colorCodes * 2);

					int numLinesBeforeCursor = StringUtils.countMatches(textBeforeCursor, "\n");

					String[] split = textBeforeCursor.split("\n");

					if (split.length == numLinesBeforeCursor) {
						thisLineBeforeCursor = "";
						textBeforeCursorWidth = 0;
					} else if (split.length > 0) {
						thisLineBeforeCursor = split[split.length - 1];
						textBeforeCursorWidth = (Minecraft.getMinecraft()).fontRendererObj
								.getStringWidth(thisLineBeforeCursor);
					} else {
						return;
					}

					String[] split2 = textNoColour.split("\n");
					if (split2.length > numLinesBeforeCursor + 1) {
						String lineAfter = split2[numLinesBeforeCursor + 1];

						String trimmed = (Minecraft.getMinecraft()).fontRendererObj.trimStringToWidth(lineAfter,
								textBeforeCursorWidth);
						int linePos = strLenNoColor(trimmed);
						if (linePos != strLenNoColor(lineAfter)) {
							char after = lineAfter.charAt(linePos);
							int trimmedWidth = (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(trimmed);
							int charWidth = (Minecraft.getMinecraft()).fontRendererObj.getCharWidth(after);
							if (trimmedWidth + charWidth / 2 < textBeforeCursorWidth) {
								linePos++;
							}
						}

						int newPos = this.textField.getSelectionEnd() - strLenNoColor(thisLineBeforeCursor)
								+ strLenNoColor(split2[numLinesBeforeCursor]) + 1 + linePos;

						if (GuiScreen.isShiftKeyDown()) {
							this.textField.setSelectionPos(newPos);
						} else {
							this.textField.setCursorPosition(newPos);
						}
					}
				}
			}

			String old = this.textField.getText();
			if ((this.options & 0x4) != 0)
				typedChar = Character.toUpperCase(typedChar);
			if ((this.options & 0x8) != 0 && typedChar == ' ')
				return;
			this.textField.setFocused(true);
			this.textField.textboxKeyTyped(typedChar, keyCode);

			if ((this.options & 0x2) != 0 && typedChar == '&') {
				int pos = this.textField.getCursorPosition() - 2;
				if (pos >= 0 && pos < this.textField.getText().length()
						&& this.textField.getText().charAt(pos) == '&') {
					String before = this.textField.getText().substring(0, pos);
					String after = "";
					if (pos + 2 < this.textField.getText().length()) {
						after = this.textField.getText().substring(pos + 2);
					}
					this.textField.setText(before + "\u00A7" + after);
					this.textField.setCursorPosition(pos + 1);
				}
			}

			if ((this.options & 0x10) != 0 && this.textField.getText().matches("[^0-9.]"))
				this.textField.setText(old);
		}
	}

	public void render(int x, int y) {
		this.x = x;
		this.y = y;
		drawTextbox(x, y, this.searchBarXSize, this.searchBarYSize, 2, this.textField, this.focus);
	}

	private void drawTextbox(int x, int y, int searchBarXSize, int searchBarYSize, int searchBarPadding,
			GuiTextField textField, boolean focus) {
		ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
		String renderText = this.prependText + textField.getText();

		GlStateManager.disableLighting();

		int paddingUnscaled = searchBarPadding / scaledresolution.getScaleFactor();
		if (paddingUnscaled < 1)
			paddingUnscaled = 1;

		int numLines = StringUtils.countMatches(renderText, "\n") + 1;
		int extraSize = (searchBarYSize - 8) / 2 + 8;
		int bottomTextBox = y + searchBarYSize + extraSize * (numLines - 1);

		int borderColour = focus ? Color.GREEN.getRGB() : Color.WHITE.getRGB();
		if (this.customBorderColour != -1) {
			borderColour = this.customBorderColour;
		}

		Gui.drawRect(x - paddingUnscaled, y - paddingUnscaled, x + searchBarXSize + paddingUnscaled,
				bottomTextBox + paddingUnscaled, borderColour);

		Gui.drawRect(x, y, x + searchBarXSize, bottomTextBox, Color.BLACK

				.getRGB());

		Pattern patternControlCode = Pattern.compile("(?i)\\u00A7([^\\u00B6\n])(?!\\u00B6)");

		String text = renderText;
		String textNoColor = renderText;
		if ((this.options & 0x2) != 0) {
			while (true) {
				Matcher matcher = patternControlCode.matcher(text);
				if (!matcher.find() || matcher.groupCount() < 1)
					break;
				String code = matcher.group(1);
				text = matcher.replaceFirst("\u00A7" + code + "\\u00B6" + code);
			}
		}
		while (true) {
			Matcher matcher = patternControlCode.matcher(textNoColor);
			if (!matcher.find() || matcher.groupCount() < 1)
				break;
			String code = matcher.group(1);
			textNoColor = matcher.replaceFirst("\\u00B6" + code);
		}

		int xStartOffset = 5;
		float scale = 1.0F;
		String[] texts = text.split("\n");
		for (int yOffI = 0; yOffI < texts.length; yOffI++) {
			int yOff = yOffI * extraSize;

			if (isScaling()
					&& (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(texts[yOffI]) > searchBarXSize - 10) {
				scale = (searchBarXSize - 2) / (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(texts[yOffI]);
				if (scale > 1.0F)
					scale = 1.0F;
				float newLen = (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(texts[yOffI]) * scale;
				xStartOffset = (int) ((searchBarXSize - newLen) / 2.0F);

				TextRenderUtils.drawStringCenteredScaledMaxWidth(texts[yOffI],
						(Minecraft.getMinecraft()).fontRendererObj, x + searchBarXSize / 2.0F,
						y + searchBarYSize / 2.0F + yOff, false, searchBarXSize - 2, Color.WHITE

								.getRGB());
			} else {
				(Minecraft.getMinecraft()).fontRendererObj.drawString(
						rage.pitclient.util.StringUtils.trimToWidth(texts[yOffI], searchBarXSize - 10),
						x + 5, y + (searchBarYSize - 8) / 2 + yOff, Color.WHITE.getRGB());
			}
		}

		if (focus && System.currentTimeMillis() % 1000L > 500L) {
			int textBeforeCursorWidth;
			String textNCBeforeCursor = textNoColor.substring(0,
					textField.getCursorPosition() + this.prependText.length());
			int colorCodes = StringUtils.countMatches(textNCBeforeCursor, "\\u00B6");
			String textBeforeCursor = text.substring(0, textField.getCursorPosition() + this.prependText.length()
					+ (((this.options & 0x2) != 0) ? (colorCodes * 2) : 0));

			int numLinesBeforeCursor = StringUtils.countMatches(textBeforeCursor, "\n");
			int yOff = numLinesBeforeCursor * extraSize;

			String[] split = textBeforeCursor.split("\n");

			if (split.length <= numLinesBeforeCursor || split.length == 0) {
				textBeforeCursorWidth = 0;
			} else {
				textBeforeCursorWidth = (int) ((Minecraft.getMinecraft()).fontRendererObj
						.getStringWidth(split[split.length - 1]) * scale);
			}
			Gui.drawRect(x + xStartOffset + textBeforeCursorWidth, y + (searchBarYSize - 8) / 2 - 1 + yOff,
					x + xStartOffset + textBeforeCursorWidth + 1, y + (searchBarYSize - 8) / 2 + 9 + yOff, Color.WHITE

							.getRGB());
		}

		String selectedText = textField.getSelectedText();
		if (!selectedText.isEmpty()) {
			int leftIndex = Math.min(textField.getCursorPosition() + this.prependText.length(),
					textField.getSelectionEnd() + this.prependText.length());
			int rightIndex = Math.max(textField.getCursorPosition() + this.prependText.length(),
					textField.getSelectionEnd() + this.prependText.length());

			float texX = 0.0F;
			int texY = 0;
			boolean sectionSignPrev = false;
			boolean bold = false;
			for (int i = 0; i < textNoColor.length(); i++) {
				char c = textNoColor.charAt(i);
				if (sectionSignPrev && c != 'k' && c != 'K' && c != 'm' && c != 'M' && c != 'n' && c != 'N' && c != 'o'
						&& c != 'O') {

					bold = (c == 'l' || c == 'L');
				}

				sectionSignPrev = false;
				if (c == '\u00B6')
					sectionSignPrev = true;

				if (c == '\n') {
					if (i >= leftIndex && i < rightIndex) {
						Gui.drawRect(x + xStartOffset + (int) texX, y + (searchBarYSize - 8) / 2 - 1 + texY,
								x + xStartOffset + (int) texX + 3, y + (searchBarYSize - 8) / 2 + 9 + texY,
								Color.LIGHT_GRAY

										.getRGB());
					}

					texX = 0.0F;
					texY += extraSize;

				} else {

					int len = (Minecraft.getMinecraft()).fontRendererObj.getStringWidth(String.valueOf(c));
					if (bold)
						len++;
					if (i >= leftIndex && i < rightIndex) {
						Gui.drawRect(x + xStartOffset + (int) texX, y + (searchBarYSize - 8) / 2 - 1 + texY,
								x + xStartOffset + (int) (texX + len * scale), y + (searchBarYSize - 8) / 2 + 9 + texY,
								Color.LIGHT_GRAY

										.getRGB());

						TextRenderUtils.drawStringScaled(String.valueOf(c), (Minecraft.getMinecraft()).fontRendererObj,
								(x + xStartOffset) + texX, y + searchBarYSize / 2.0F - scale * 8.0F / 2.0F + texY,
								false, Color.BLACK

										.getRGB(),
								scale);
						if (bold) {
							TextRenderUtils.drawStringScaled(String.valueOf(c),
									(Minecraft.getMinecraft()).fontRendererObj, (x + xStartOffset) + texX + 1.0F,
									y + searchBarYSize / 2.0F - scale * 8.0F / 2.0F + texY, false, Color.BLACK

											.getRGB(),
									scale);
						}
					}

					texX += len * scale;
				}
			}
		}
	}
}
