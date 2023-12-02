package rage.pitclient.clickgui.elements.menu;

import java.awt.Color;

import rage.pitclient.clickgui.elements.Element;
import rage.pitclient.clickgui.elements.ModuleButton;
import rage.pitclient.clickgui.util.ColorUtil;
import rage.pitclient.clickgui.util.FontUtil;
import rage.pitclient.clickgui.util.GuiFix;
import rage.pitclient.settings.Setting;

public class ElementCheckBox extends Element {
	/*
	 * Konstrukor
	 */
	public ElementCheckBox(ModuleButton iparent, Setting iset) {
		parent = iparent;
		set = iset;
		super.setup();
	}

	/*
	 * Rendern des Elements 
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Color temp = ColorUtil.getClickGUIColor();
		int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 200).getRGB();
		
		/*
		 * Die Box und Umrandung rendern
		 */
		GuiFix.drawRect(x, y, x + width, y + height, 0xff1a1a1a);

		/*
		 * Titel und Checkbox rendern.
		 */
		FontUtil.drawString(setstrg, x + width - FontUtil.getStringWidth(setstrg)- 1.5, y + FontUtil.getFontHeight() / 2 - 1.5, 0xffffffff);
		GuiFix.drawRect(x + 3, y + 4, x + 10, y + 11, set.getValBoolean() ? color : 0xff000000);
		if (isCheckHovered(mouseX, mouseY))
			GuiFix.drawRect(x + 2, y + 2, x + 12, y + 12, 0x55111111);
	}

	/*
	 * 'true' oder 'false' bedeutet hat der Nutzer damit interagiert und
	 * sollen alle anderen Versuche der Interaktion abgebrochen werden?
	 */
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0 && isCheckHovered(mouseX, mouseY)) {
			set.setValBoolean(!set.getValBoolean());
			return true;
		}
		
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/*
	 * Einfacher HoverCheck, benötigt damit die Value geändert werden kann
	 */
	public boolean isCheckHovered(int mouseX, int mouseY) {
		return mouseX >= x + 1 && mouseX <= x + 12 && mouseY >= y + 2 && mouseY <= y + 13;
	}
}
