package rage.pitclient.clickgui.elements.menu;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import rage.pitclient.PitClient;
import rage.pitclient.clickgui.elements.Element;
import rage.pitclient.clickgui.elements.ModuleButton;
import rage.pitclient.clickgui.util.ColorUtil;
import rage.pitclient.clickgui.util.FontUtil;
import rage.pitclient.clickgui.util.GuiFix;
import rage.pitclient.settings.Setting;

public class ElementComboBox extends Element {

	public ElementComboBox(ModuleButton iparent, Setting iset) {
		parent = iparent;
		set = iset;
		super.setup();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Color temp = ColorUtil.getClickGUIColor();
		int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 150).getRGB();

		GuiFix.drawRect(x, y, x + width, y + height, 0xff1a1a1a);

		FontUtil.drawTotalCenteredString(setstrg, x + width / 2, y + 13/2, 0xffffffff);
		int clr1 = color;
		int clr2 = temp.getRGB();

		GuiFix.drawRect(x, y + 14, x + width, y + 15, 0x77000000);
		
		String plus = "+";
		if (comboextended) {
			plus = "-";
			//GuiFix.drawRect(x + width - 5.5, y + 3.5, x + width - 0.5, y + height - 2.5, temp.getRGB());
		} //else GuiFix.drawRect(x + width - 5.5, y + 3.5, x + width - 0.5, y + height - 2.5, temp.getRGB());
		
		FontUtil.drawTotalCenteredString(plus, x + width - 3.5, y + 13/2, temp.getRGB());
		
		if (comboextended) {
			GuiFix.drawRect(x, y + 15, x + width, y + height, 0xaa121212);
			double ay = y + 15;
			for (String sld : set.getOptions()) {
				String elementtitle = sld.substring(0, 1).toUpperCase() + sld.substring(1, sld.length());
				//FontUtil.drawCenteredString(elementtitle, x + width / 2, ay + 0.5, 0xffffffff);
				FontUtil.drawString(elementtitle, x + width - FontUtil.getStringWidth(elementtitle) - 1.5, ay + 0.5, 0xffffffff);

				if (sld.equalsIgnoreCase(set.getValString())) {
					GuiFix.drawRect(x, ay, x + 1.5, ay + FontUtil.getFontHeight() + 2, clr1);
				}

				if (mouseX >= x && mouseX <= x + width && mouseY >= ay && mouseY < ay + FontUtil.getFontHeight() + 2) {
					GuiFix.drawRect(x + width - 1.2, ay, x + width, ay + FontUtil.getFontHeight() + 2, clr2);
				}
				ay += FontUtil.getFontHeight() + 2;
			}
		}
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 1) {
			if (isButtonHovered(mouseX, mouseY)) {
				comboextended = !comboextended;
				return true;
			}
		}
		if (mouseButton == 0) {
			if (!comboextended) return false;
			double ay = y + 15;
			for (String slcd : set.getOptions()) {
				if (mouseX >= x && mouseX <= x + width && mouseY >= ay && mouseY <= ay + FontUtil.getFontHeight() + 2) {
					if(PitClient.settingsManager.getSettingByName("Sound").getValBoolean())
					Minecraft.getMinecraft().thePlayer.playSound("tile.piston.in", 20.0F, 20.0F);
					
					if(clickgui != null && clickgui.setmgr != null)
					this.parent.mod.getInternalSetting(set.getName()).setValString(slcd);
					return true;
				}
				ay += FontUtil.getFontHeight() + 2;
			}
		}
		

		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	public boolean isButtonHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 15;
	}
}
