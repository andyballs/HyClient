package rage.pitclient.newgui.settings.type;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import rage.pitclient.newgui.ModuleButton;
import rage.pitclient.newgui.NewGui;
import rage.pitclient.newgui.settings.GuiSetting;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.RenderUtils;

public class SliderSetting extends GuiSetting {
	
	private boolean dragging;
	
	public SliderSetting(Setting set, ModuleButton mb, NewGui parent) {
		this.setting = set;
		this.mb = mb;
		this.parent = parent;
		this.width = 150;
		this.height = 11;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int green = new Color(10,200,10,100).getRGB();
		int gray = new Color(128, 128, 128, 100).getRGB();

		
		
		RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, Color.black.getRGB(), gray);
		
		float percent = (float) ((setting.getValDouble() - setting.getMin())/(setting.getMax() - setting.getMin()));
		
		RenderUtils.drawRect(x, y + 0.5f, x + (150 * percent), y + height - 0.5f, green);
		RenderUtils.drawRect(x + (150 * percent), y + 0.5f, x + (150 * percent) + 1, y + height - 0.5f, Color.black.getRGB());
		
		String displayval = "" + Math.round(setting.getValDouble() * 100D)/ 100D;
		Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(displayval, x + (150 * percent) + 2, y+ 2, 0xa0a0a0);
	
		if (dragging) {
			double diff = setting.getMax() - setting.getMin();
			double val = setting.getMin() + (MathHelper.clamp_double((mouseX - x) / (double)width, 0, 1)) * diff;
			setting.setValDouble(val);
		}
	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {		
		if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
			
			dragging = true;
			return true;
		}
		return false;
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		dragging = false;
	}

}
