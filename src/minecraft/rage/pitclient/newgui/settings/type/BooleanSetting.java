package rage.pitclient.newgui.settings.type;

import java.awt.Color;

import rage.pitclient.newgui.ModuleButton;
import rage.pitclient.newgui.NewGui;
import rage.pitclient.newgui.settings.GuiSetting;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.RenderUtils;

public class BooleanSetting extends GuiSetting {

	public BooleanSetting(Setting set, ModuleButton mb, NewGui parent) {
		this.setting = set;
		this.mb = mb;
		this.parent = parent;
		this.width = 11;
		this.height = 11;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int green = new Color(10,200,10,100).getRGB();
		int gray = new Color(128, 128, 128, 100).getRGB();

		RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, Color.black.getRGB(), gray);
		
		if (setting.getValBoolean()) {
			RenderUtils.drawRect(x + 0.5f, y + 0.5f, x + width - 0.5f, y + height - 0.5f, green);
		}
	
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {		
		if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
			setting.setValBoolean(!setting.getValBoolean());
			return true;
		}
		return false;
	}

}
