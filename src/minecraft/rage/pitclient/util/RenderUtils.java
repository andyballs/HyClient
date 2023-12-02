package rage.pitclient.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtils {
	
	public static void drawFloatingRect(int x, int y, int width, int height) {
		drawFloatingRect(x, y, width, height, -4144960, -986896, -7303024);
	}
	
	public static void drawFloatingRect(int x, int y, int width, int height, int rectColor, int secondaryShadow, int topShadow) {
		GlStateManager.enableDepth();

		GlStateManager.translate(0.0F, 0.0F, 1.0F);

		Gui.drawRect(x + 2, y + 2, x + width + 2, y + height + 2, -1610612736);

		GlStateManager.depthFunc(513);
		GlStateManager.translate(0.0F, 0.0F, 1.0F);
		Gui.drawRect(x + 1, y + 1, x + width - 1, y + height - 1, rectColor);

		Gui.drawRect(x, y, x + width - 1, y + height - 1, secondaryShadow);

		Gui.drawRect(x, y, x + width, y + height, topShadow);

		GlStateManager.depthFunc(515);

		GlStateManager.translate(0.0F, 0.0F, -2.0F);

		GlStateManager.disableDepth();
	}

	public static void drawTexturedRect(float x, float y, float width, float height) {
		drawTexturedRect(x, y, width, height, 0.0F, 1.0F, 0.0F, 1.0F);
	}

	public static void drawTexturedRect(float x, float y, float width, float height, int filter) {
		drawTexturedRect(x, y, width, height, 0.0F, 1.0F, 0.0F, 1.0F, filter);
	}

	public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin,
			float vMax) {
		drawTexturedRect(x, y, width, height, uMin, uMax, vMin, vMax, 9728);
	}

	public static void drawTexturedRect(float x, float y, float width, float height, float uMin, float uMax, float vMin,
			float vMax, int filter) {
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GL14.glBlendFuncSeparate(770, 771, 1, 771);

		GL11.glTexParameteri(3553, 10241, filter);
		GL11.glTexParameteri(3553, 10240, filter);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(x, (y + height), 0.0D).tex(uMin, vMax).endVertex();
		worldrenderer.pos((x + width), (y + height), 0.0D).tex(uMax, vMax).endVertex();
		worldrenderer.pos((x + width), y, 0.0D).tex(uMax, vMin).endVertex();
		worldrenderer.pos(x, y, 0.0D).tex(uMin, vMin).endVertex();
		tessellator.draw();

		GL11.glTexParameteri(3553, 10241, 9728);
		GL11.glTexParameteri(3553, 10240, 9728);

		GlStateManager.disableBlend();
	}

	public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor,
			int endColor) {
		float startAlpha = (startColor >> 24 & 0xFF) / 255.0F;
		float startRed = (startColor >> 16 & 0xFF) / 255.0F;
		float startGreen = (startColor >> 8 & 0xFF) / 255.0F;
		float startBlue = (startColor & 0xFF) / 255.0F;
		float endAlpha = (endColor >> 24 & 0xFF) / 255.0F;
		float endRed = (endColor >> 16 & 0xFF) / 255.0F;
		float endGreen = (endColor >> 8 & 0xFF) / 255.0F;
		float endBlue = (endColor & 0xFF) / 255.0F;

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		worldrenderer.pos(left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		worldrenderer.pos(left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
		worldrenderer.pos(right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
		tessellator.draw();

		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
	
    public static void startDrawing() {
        GL11.glEnable(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
    }
    
    public static void stopDrawing() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }
    
    public static void drawRect(float g, float y, float h, float k, int color)
    {
        if (g < h)
        {
            float i = g;
            g = h;
            h = i;
        }

        if (y < k)
        {
            float j = y;
            y = k;
            k = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos((double)g, (double)k, 0.0D).endVertex();
        worldrenderer.pos((double)h, (double)k, 0.0D).endVertex();
        worldrenderer.pos((double)h, (double)y, 0.0D).endVertex();
        worldrenderer.pos((double)g, (double)y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.resetColor();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
	public static void drawBorderedRect(final float x, final float y, final float x2, final float y2, final float l1, final int col1, final int col2) {
        drawRect(x, y, x2, y2, col2);
        final float f = (col1 >> 24 & 0xFF) / 255.0f;
        final float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        final float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        final float f4 = (col1 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)y);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glVertex2d((double)x2, (double)y);
        GL11.glVertex2d((double)x, (double)y2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }
}
