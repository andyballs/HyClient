package rage.pitclient.newgui;

import java.awt.Color;
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import rage.pitclient.util.RenderUtils;
import rage.pitclient.util.TextRenderUtils;

public class CategoryPanel {
	public String title;
	public float x;
	public float y;
	public float width;
	public float height;
	public boolean selected;
	public ArrayList<ModuleButton> modules = new ArrayList<>();
	public NewGui parent;

	public CategoryPanel(String ititle, float iwidth, float iheight, boolean selected, NewGui parent) {
		this.title = ititle;
		this.width = iwidth;
		this.height = iheight;
		this.selected = selected;
		this.parent = parent;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int gray = new Color(80, 80, 80, 100).getRGB();
		RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, Color.black.getRGB(), gray);
		if (selected) {
			int sel = new Color(150, 80, 80, 100).getRGB();
			RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, Color.black.getRGB(), sel);
		}
		TextRenderUtils.drawStringCenteredScaledMaxWidth(title, Minecraft.getMinecraft().fontRendererObj, x + width/2, y + height/2, true, 120, 0xa0a0a0);
		
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
			return true;
		}
		return false;
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}

