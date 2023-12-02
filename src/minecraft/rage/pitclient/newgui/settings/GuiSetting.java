package rage.pitclient.newgui.settings;

import rage.pitclient.newgui.ModuleButton;
import rage.pitclient.newgui.NewGui;
import rage.pitclient.settings.Setting;

public class GuiSetting {
	
	public Setting setting;
	public ModuleButton mb;
	public NewGui parent;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {}
	public void mouseReleased(int mouseX, int mouseY, int state) {}
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		return isHovered(mouseX, mouseY);
	}
	
	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
}
