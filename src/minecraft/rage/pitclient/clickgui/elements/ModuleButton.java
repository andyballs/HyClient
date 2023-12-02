package rage.pitclient.clickgui.elements;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import rage.pitclient.PitClient;
import rage.pitclient.clickgui.Panel;
import rage.pitclient.clickgui.elements.menu.ElementCheckBox;
import rage.pitclient.clickgui.elements.menu.ElementComboBox;
import rage.pitclient.clickgui.elements.menu.ElementSlider;
import rage.pitclient.clickgui.util.ColorUtil;
import rage.pitclient.clickgui.util.FontUtil;
import rage.pitclient.clickgui.util.GuiFix;
import rage.pitclient.module.Module;
import rage.pitclient.settings.Setting;


public class ModuleButton {
	public Module mod;
	public ArrayList<Element> menuelements;
	public Panel parent;
	public double x;
	public double y;
	public double width;
	public double height;
	public boolean extended = false;
	public boolean listening = false;

	public ModuleButton(Module imod, Panel pl) {
		mod = imod;
		height = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2;
		parent = pl;
		menuelements = new ArrayList<>();

		if (PitClient.settingsManager.getSettingsByMod(imod) != null)
			for (Setting s : PitClient.settingsManager.getSettingsByMod(imod)) {
				if (s.isCheck()) {
					menuelements.add(new ElementCheckBox(this, s));
				} else if (s.isSlider()) {
					menuelements.add(new ElementSlider(this, s));
				} else if (s.isCombo()) {
					menuelements.add(new ElementComboBox(this, s));
				}
			}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		Color temp = ColorUtil.getClickGUIColor();
		int color = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 150).getRGB();
		//if (extended) color = temp.brighter().getRGB();

		int textcolor = 0xffafafaf;
		if (mod.isEnabled()) {
			GuiFix.drawRect(x - 2, y, x + width + 2, y + height + 1, color);
			textcolor = 0xffefefef;
		}
		
		if (menuelements.size() > 0) {
			String plus = "+";
			if (extended) {
				plus = "-";
				//GuiFix.drawRect(x + width - 5.5, y + 3.5, x + width - 0.5, y + height - 2.5, temp.getRGB());
			} //else GuiFix.drawRect(x + width - 5.5, y + 3.5, x + width - 0.5, y + height - 2.5, temp.getRGB());
			
			FontUtil.drawTotalCenteredString(plus, x + width - 2.5, y + height / 2, Color.black.getRGB());
		}
		
		if (mod.getDisableOnVanish()) {
			FontUtil.drawTotalCenteredString("V", x + 2.5, y + height / 2, textcolor);
		}

		if (isHovered(mouseX, mouseY)) {
			GuiFix.drawRect(x - 2, y, x + width + 2, y + height + 1, 0x55111111);
		}

		String bind = Keyboard.getKeyName(mod.getKeybind());
		String display = (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && mod.getKeybind() != 0) ? "[" + bind + "] " + mod.getName() : mod.getName();
		
		if (listening) {
			FontUtil.drawTotalCenteredStringWithShadow("Bind...", x + width / 2, y + height / 2, textcolor);
		} else {
			FontUtil.drawTotalCenteredStringWithShadow(display, x + width / 2, y + height / 2, textcolor);
		}
	}

	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!isHovered(mouseX, mouseY))
			return false;
		if (mouseButton == 0) {
			mod.toggle();
			
			if (PitClient.settingsManager.getSettingByName("Sound").getValBoolean()) {
				Minecraft.getMinecraft().thePlayer.playSound("random.click", 0.5f, 0.5f);
			}
		} else if (mouseButton == 1) {
			if (listening) {
				if (!mod.getName().equalsIgnoreCase("Config")) PitClient.commandManager.sendMessageWithPrefix("Unbound " + mod.getName() + "");
				mod.setKeybind(Keyboard.KEY_NONE);
				listening = false;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !listening) {
				listening = true;
			} else if (menuelements != null && menuelements.size() > 0) {
				boolean b = !this.extended;
				parent.closeAllSettings();
				this.extended = b;
				
				if (PitClient.settingsManager.getSettingByName("Sound").getValBoolean())
				if (extended) Minecraft.getMinecraft().thePlayer.playSound("tile.piston.out", 1f, 1f); 
				else Minecraft.getMinecraft().thePlayer.playSound("tile.piston.in", 1f, 1f);
			}
		} else if (mouseButton == 2) {
			mod.setDisableOnVanish(!mod.getDisableOnVanish());
		}
		return true;
	}

	public boolean keyTyped(char typedChar, int keyCode) throws IOException {

		if (listening && keyCode != Keyboard.KEY_LSHIFT) {
			if (keyCode != Keyboard.KEY_ESCAPE) {
				PitClient.commandManager.sendMessageWithPrefix("Bound " + mod.getName() + "" + " to " + Keyboard.getKeyName(keyCode) + "");
				
				mod.setKeybind(keyCode);
			} else {
				listening = false;
			}
			listening = false;
			return true;
		}
		return false;
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

}
