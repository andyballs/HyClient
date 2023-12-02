package rage.pitclient.newgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.module.Module;
import rage.pitclient.newgui.settings.GuiSetting;
import rage.pitclient.util.RenderUtils;
import rage.pitclient.util.TextRenderUtils;

public class ModuleButton {

	public final Module module;
	private final NewGui parent;
	
	public int x;
	public int y;
	public int width = 130;
	public int height = 20;
	public boolean selected;
	public boolean binding;
	
	public ArrayList<GuiSetting> settings = new ArrayList<>();
	
	public ModuleButton(Module mod, NewGui parent) {
		this.module = mod;
		this.parent = parent;
	}
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int gray = new Color(80, 80, 80, 100).getRGB();
		int red = new Color(150, 80, 80, 100).getRGB();
		int green = new Color(10, 130, 10, 100).getRGB();
		RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, Color.black.getRGB(), gray);
		
//		if (selected && module.enabled) {
//			RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, Color.black.getRGB(), red);
//		} else if (selected) {
//			RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, Color.black.getRGB(), red);
//		} else if (module.enabled) {
//			RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, Color.black.getRGB(), red);
//		}
		if (module.enabled) {
			RenderUtils.drawBorderedRect(x, y, x + width, y + height, 2, Color.black.getRGB(), green);
		}
		
		int c = selected ? red : 0xa0a0a0;
		String name = module.getName();
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && module.getKeybind() != 0) {
			name = "[" + Keyboard.getKeyName(module.getKeybind()) + "] " + module.getName();
		}
		if (binding) name = "Press key to bind...";
		
		if (module.getDisableOnVanish()) {
	 		TextRenderUtils.drawStringCentered(EnumChatFormatting.GOLD + "V", Minecraft.getMinecraft().fontRendererObj, x + 10, y + height/2+1, true, c);
		}
		
 		TextRenderUtils.drawStringCentered(name, Minecraft.getMinecraft().fontRendererObj, x + width/2, y + height/2+1, true, c);
		
	}
	
	public boolean keyTyped(char typedChar, int keyCode) throws IOException {
		if (binding && keyCode != Keyboard.KEY_LSHIFT) {
			if (keyCode != Keyboard.KEY_ESCAPE) {
				PitClient.commandManager.sendModuleMessage(module,"Bound to " + Keyboard.getKeyName(keyCode));
				
				module.setKeybind(keyCode);
			} else {
				binding = false;
			}
			binding = false;
			return true;
		}
		return false;
	}
	
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if (!isHovered(mouseX, mouseY)) return false;
		if (mouseButton == 0) {
			return true;
		}
		if (mouseButton == 1) {
			if (binding) {
				PitClient.commandManager.sendModuleMessage(module, "Unbound");
				module.setKeybind(Keyboard.KEY_NONE);
				binding = false;
			}
			else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !binding) {
				binding = true;
			} 
		} else if (mouseButton == 2) {
			module.setDisableOnVanish(!module.getDisableOnVanish());
		}
		return false;
	}

	public boolean isHovered(int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}
	
}
