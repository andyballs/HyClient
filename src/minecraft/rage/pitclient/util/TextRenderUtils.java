package rage.pitclient.util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class TextRenderUtils {
	public static int getCharVertLen(char c) {
		if ("acegmnopqrsuvwxyz".indexOf(c) >= 0) {
			return 5;
		}
		return 7;
	}

	public static float getVerticalHeight(String str) {
		str = StringUtils.cleanColour(str);
		float height = 0.0F;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			int charHeight = getCharVertLen(c);
			height += charHeight + 1.5F;
		}
		return height;
	}

	public static void drawStringVertical(String str, FontRenderer fr, float x, float y, boolean shadow, int colour) {
		String format = FontRenderer.getFormatFromString(str);
		str = StringUtils.cleanColour(str);
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			int charHeight = getCharVertLen(c);
			int charWidth = fr.getCharWidth(c);
			fr.drawString(format + c, x + (5 - charWidth) / 2.0F, y - 7.0F + charHeight, colour, shadow);

			y += charHeight + 1.5F;
		}
	}

	public static void drawStringScaledMaxWidth(String str, FontRenderer fr, float x, float y, boolean shadow, int len,
			int colour) {
		int strLen = fr.getStringWidth(str);
		float factor = len / strLen;
		factor = Math.min(1.0F, factor);

		drawStringScaled(str, fr, x, y, shadow, colour, factor);
	}

	public static void drawStringCentered(String str, FontRenderer fr, float x, float y, boolean shadow, int colour) {
		int strLen = fr.getStringWidth(str);

		float x2 = x - strLen / 2.0F;
		float y2 = y - fr.FONT_HEIGHT / 2.0F;

		GL11.glTranslatef(x2, y2, 0.0F);
		fr.drawString(str, 0.0F, 0.0F, colour, shadow);
		GL11.glTranslatef(-x2, -y2, 0.0F);
	}

	public static void drawStringScaled(String str, FontRenderer fr, float x, float y, boolean shadow, int colour,
			float factor) {
		GlStateManager.scale(factor, factor, 1.0F);
		fr.drawString(str, x / factor, y / factor, colour, shadow);
		GlStateManager.scale(1.0F / factor, 1.0F / factor, 1.0F);
	}

	public static void drawStringCenteredScaledMaxWidth(String str, FontRenderer fr, float x, float y, boolean shadow,
			int len, int colour) {
		int strLen = fr.getStringWidth(str);
		float factor = len / strLen;
		factor = Math.min(1.0F, factor);
		int newLen = Math.min(strLen, len);

		float fontHeight = 8.0F * factor;

		drawStringScaled(str, fr, x - (newLen / 2), y - fontHeight / 2.0F, shadow, colour, factor);
	}

	public static void renderToolTip(ItemStack stack, int mouseX, int mouseY, int screenWidth, int screenHeight,
			FontRenderer fontStd) {
		List<String> list = stack.getTooltip((EntityPlayer) (Minecraft.getMinecraft()).thePlayer,
				(Minecraft.getMinecraft()).gameSettings.advancedItemTooltips);

		for (int i = 0; i < list.size(); i++) {
			if (i == 0) {
				list.set(i, (stack.getRarity()).rarityColor + (String) list.get(i));
			} else {
				list.set(i, EnumChatFormatting.GRAY + (String) list.get(i));
			}
		}

		FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
		drawHoveringText(list, mouseX, mouseY, screenWidth, screenHeight, -1, (font == null) ? fontStd : font);
	}

	public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth,
			int screenHeight, int maxTextWidth, FontRenderer font) {
		if (!textLines.isEmpty()) {
			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int tooltipTextWidth = 0;

			for (String textLine : textLines) {
				int textLineWidth = font.getStringWidth(textLine);

				if (textLineWidth > tooltipTextWidth) {
					tooltipTextWidth = textLineWidth;
				}
			}

			boolean needsWrap = false;

			int titleLinesCount = 1;
			int tooltipX = mouseX + 12;
			if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
				tooltipX = mouseX - 16 - tooltipTextWidth;
				if (tooltipX < 4) {

					if (mouseX > screenWidth / 2) {
						tooltipTextWidth = mouseX - 12 - 8;
					} else {
						tooltipTextWidth = screenWidth - 16 - mouseX;
					}
					needsWrap = true;
				}
			}

			if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
				tooltipTextWidth = maxTextWidth;
				needsWrap = true;
			}

			if (needsWrap) {
				int wrappedTooltipWidth = 0;
				List<String> wrappedTextLines = new ArrayList<>();
				for (int i = 0; i < textLines.size(); i++) {
					String textLine = textLines.get(i);
					List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
					if (i == 0) {
						titleLinesCount = wrappedLine.size();
					}

					for (String line : wrappedLine) {
						int lineWidth = font.getStringWidth(line);
						if (lineWidth > wrappedTooltipWidth) {
							wrappedTooltipWidth = lineWidth;
						}
						wrappedTextLines.add(line);
					}
				}
				tooltipTextWidth = wrappedTooltipWidth;
				textLines = wrappedTextLines;

				if (mouseX > screenWidth / 2) {
					tooltipX = mouseX - 16 - tooltipTextWidth;
				} else {
					tooltipX = mouseX + 12;
				}
			}

			int tooltipY = mouseY - 12;
			int tooltipHeight = 8;

			if (textLines.size() > 1) {
				tooltipHeight += (textLines.size() - 1) * 10;
				if (textLines.size() > titleLinesCount) {
					tooltipHeight += 2;
				}
			}

			if (tooltipY + tooltipHeight + 6 > screenHeight) {
				tooltipY = screenHeight - tooltipHeight - 6;
			}

			//int zLevel = 300;
			//int backgroundColor = -267386864;
			RenderUtils.drawGradientRect(300, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3,
					-267386864, -267386864);
			RenderUtils.drawGradientRect(300, tooltipX - 3, tooltipY + tooltipHeight + 3,
					tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, -267386864, -267386864);
			RenderUtils.drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3,
					tooltipY + tooltipHeight + 3, -267386864, -267386864);
			RenderUtils.drawGradientRect(300, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3,
					-267386864, -267386864);
			RenderUtils.drawGradientRect(300, tooltipX + tooltipTextWidth + 3, tooltipY - 3,
					tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, -267386864, -267386864);
			//int borderColorStart = 1347420415;
			//int borderColorEnd = 1344798847;
			RenderUtils.drawGradientRect(300, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1,
					tooltipY + tooltipHeight + 3 - 1, 1347420415, 1344798847);
			RenderUtils.drawGradientRect(300, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1,
					tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, 1347420415, 1344798847);
			RenderUtils.drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3,
					tooltipY - 3 + 1, 1347420415, 1347420415);
			RenderUtils.drawGradientRect(300, tooltipX - 3, tooltipY + tooltipHeight + 2,
					tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, 1344798847, 1344798847);

			for (int lineNumber = 0; lineNumber < textLines.size(); lineNumber++) {
				String line = textLines.get(lineNumber);
				font.drawStringWithShadow(line, tooltipX, tooltipY, -1);

				if (lineNumber + 1 == titleLinesCount) {
					tooltipY += 2;
				}

				tooltipY += 10;
			}

			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableRescaleNormal();
		}
		GlStateManager.disableLighting();
	}
}
