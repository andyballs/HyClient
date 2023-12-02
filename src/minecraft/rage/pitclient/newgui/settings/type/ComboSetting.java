package rage.pitclient.newgui.settings.type;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import rage.pitclient.clickgui.util.FontUtil;
import rage.pitclient.newgui.ModuleButton;
import rage.pitclient.newgui.NewGui;
import rage.pitclient.newgui.settings.GuiSetting;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.RenderUtils;
import rage.pitclient.util.TextRenderUtils;

public class ComboSetting extends GuiSetting {
	
	public boolean extended;

	public ComboSetting(Setting set, ModuleButton mb, NewGui parent) {
		this.setting = set;
		this.mb = mb;
		this.parent = parent;
		this.width = 70;
		this.height = 13;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int green = new Color(10, 200, 10, 100).getRGB();
		int gray = new Color(128, 128, 128, 100).getRGB();

		int len = Minecraft.getMinecraft().fontRendererObj.getStringWidth(setting.getValString()) + 10;
		if (len > width) width = len;
		int c = extended ? green : Color.black.getRGB();
		RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, c, gray);
		TextRenderUtils.drawStringCentered(setting.getValString(), Minecraft.getMinecraft().fontRendererObj, x + width/2, y + 7f, true, 0xa0a0a0);
	
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {		
		if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
			extended = !extended;
			return true;
		}
		if (mouseButton == 0 && extended) {
			double ay = y + 15;
			for (String slcd : setting.getOptions()) {
				//if (slcd.equalsIgnoreCase(setting.getValString())) continue;
				if (mouseX >= x && mouseX <= x + width && mouseY >= ay && mouseY <= ay + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2) {					
					setting.setValString(slcd);
					return true;
				}
				ay += FontUtil.getFontHeight() + 2;
			}
		}

		return false;
	}

}
