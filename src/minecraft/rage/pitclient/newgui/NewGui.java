package rage.pitclient.newgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import rage.pitclient.PitClient;
import rage.pitclient.events.ClickGuiSettingEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.newgui.settings.GuiSetting;
import rage.pitclient.newgui.settings.type.BooleanSetting;
import rage.pitclient.newgui.settings.type.ComboSetting;
import rage.pitclient.newgui.settings.type.SliderSetting;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.RenderUtils;
import rage.pitclient.util.TextRenderUtils;

public class NewGui extends GuiScreen {

	private int x;
	private int y;
	
	private int GUI_LENGTH = 600;
	private int GUI_HEIGHT = 400;
	
	private ArrayList<CategoryPanel> panels;
	
	private boolean open;
	private long openMilli;
	
	private CategoryPanel selectedPanel;
	private ModuleButton selectedModule;
	
	public NewGui() {
		
		panels = new ArrayList<>();
		
		ArrayList<String> pre = new ArrayList<>();
		
		for (Category c : Category.values()) {
			if (c == Category.INVISIBLE | c == Category.CONFIG) continue;
			String title = Character.toUpperCase(c.name().toLowerCase().charAt(0)) + c.name().toLowerCase().substring(1);
			pre.add(title);
		}
		
		pre.add("Gui");
		
		int pwidth = (GUI_LENGTH - 20 - 15)/pre.size();
		
		for (String s : pre) {
			panels.add(new CategoryPanel(s, pwidth, 40, false, this));
		}
		
		for (CategoryPanel p : panels) {
			for (Module m : PitClient.moduleManager.getModules()) {
				if (m.getCategory().toString().equalsIgnoreCase(p.title)) {
					p.modules.add(new ModuleButton(m, this));
				}
			}
			for (ModuleButton mb : p.modules) {
				for (Setting s : mb.module.settings) {
					if (s.isSlider()) mb.settings.add(new SliderSetting(s, mb, this));
					if (s.isCheck()) mb.settings.add(new BooleanSetting(s, mb, this));
					if (s.isCombo()) mb.settings.add(new ComboSetting(s, mb, this));
				}
			}
			if (p.title.equalsIgnoreCase("Gui")) {
				
			}
		}
		


	}
	
	@Override
	public void initGui() {
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		x = sr.getScaledWidth()/2 - GUI_LENGTH/2;
		y = sr.getScaledHeight()/2 - GUI_HEIGHT/2;
		
		int px = x + 10;
		int pwidth = (GUI_LENGTH - 20 - 15)/panels.size();

		for (CategoryPanel panel : panels) {
			panel.x = px;
			panel.y = y + 20;
			panel.width = pwidth;
			px += pwidth + 3;
			
			int mx = x + 10 + 10;
			int my = y + 80 + 8;
			
			for (ModuleButton mb : panel.modules) {
				mb.x = mx;
				mb.y = my;
				my += mb.height + 5;
				int i = 0;
				for (GuiSetting s : mb.settings) {
			    	int modRight = x + 10 + 150;
			    	int modTop = y + 80;
					int x = modRight + 10 + 10 + (i % 2 == 0 ? 0 : 200);
					int y = modTop + 20 + (i % 2 == 0 ? i+1 : i)*15;
					
					s.x = x;
					s.y = y + 13;
					
					i++;
				}
			}
		}
		
		if (!open) openMilli = System.currentTimeMillis();
		open = true;
	}
	
	@Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		int dark_gray = new Color(64, 64, 64, 100).getRGB();
		int gray = new Color(80, 80, 80, 100).getRGB();
		int green = new Color(10, 200, 10, 100).getRGB();
		
		
		long timeDelta = System.currentTimeMillis() - openMilli;
		
		int xSize = GUI_LENGTH/2;
		int ySize = GUI_HEIGHT/2;
		
    	int yOff = 3;
    	int xOff = 2;
    	
    	int halfOpeningMs = 150;
		
        if (timeDelta < halfOpeningMs) {
            xSize = (int)(timeDelta * xSize / halfOpeningMs);
            ySize = 3;
            yOff = 0;
        } else if (timeDelta < halfOpeningMs * 2) {
			ySize = (int)(timeDelta - halfOpeningMs) * (ySize) / halfOpeningMs;
		}
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		
		//RenderUtils.drawRect(x, y, x+GUI_LENGTH+ 2, y+GUI_HEIGHT+2, new Color(100,20,20,200).getRGB());
    	GL11.glEnable(GL11.GL_SCISSOR_TEST);
		
    	int left = (x + GUI_LENGTH/2 - xSize) * sr.getScaleFactor();
    	int bottom = sr.getScaledHeight() - (ySize + yOff)* 2;
    	int w = (xSize + xOff) * 2 * sr.getScaleFactor();
    	int h = (ySize + yOff) * 2 * sr.getScaleFactor();
    	
    	
		GL11.glScissor(left, bottom, w, h);
		
    	RenderUtils.drawFloatingRect(x, y, GUI_LENGTH, GUI_HEIGHT, dark_gray, gray, Color.black.getRGB());
    	int modLeft = x + 10;
    	int modRight = modLeft + 150;
    	int modTop = y + 80;
    	int modBottom = y + GUI_HEIGHT - 10;
    	
    	RenderUtils.drawBorderedRect(modLeft, modTop, modRight, modBottom, 2, Color.black.getRGB(), gray);    	
    	
    	RenderUtils.drawBorderedRect(modRight + 10, modTop + 20 + 5, x + GUI_LENGTH - 10, modBottom, 2, Color.black.getRGB(), gray);

    	RenderUtils.drawBorderedRect(modRight + 10, modTop, x + GUI_LENGTH - 10, modTop + 20, 2, Color.black.getRGB(), gray);
    	
    	for (CategoryPanel panel : panels) {
    		panel.drawScreen(mouseX, mouseY, partialTicks);
//    		if (panel.isHovered(mouseX, mouseY)) {
//    			panel.selected = true;
//    			selectedPanel = panel;
//    		}
    	}
    	
//    	for (CategoryPanel panel : panels) {
//    		if (selectedPanel != panel) panel.selected = false;
//    	}
    	
    	if (selectedPanel != null) {    		
        	if (selectedPanel.title.equals("Gui")) {
        		TextRenderUtils.drawStringScaled("COMING SOON", Minecraft.getMinecraft().fontRendererObj, modRight + 70, modTop + 80, true, green, 5);
        		return;
        	}
        	for (ModuleButton mb : selectedPanel.modules) {
        		mb.drawScreen(mouseX, mouseY, partialTicks);
        		if (mb.isHovered(mouseX, mouseY)) {
        			mb.selected = true;
        			selectedModule = mb;
        		}
        	}
        	
        	for (ModuleButton mb : selectedPanel.modules) {
        		if (selectedModule != mb) mb.selected = false;
        	}
        	
        	if (selectedModule != null) {
        		String tip = selectedModule.module.getTooltip();
        		for (GuiSetting s : selectedModule.settings) {
        			Minecraft.getMinecraft().fontRendererObj.drawString(s.setting.getName(), s.x, s.y - 13, 0xa0a0a0, true);
        			s.drawScreen(mouseX, mouseY, partialTicks);
        			if (mouseX >= s.x && mouseX <= s.x + 170 && mouseY >= s.y - 10 && mouseY <= s.y + s.height) {
        				tip = s.setting.getTooltip();
        			}
        		}
        		
        		for (GuiSetting s : selectedModule.settings) {
        			if (s instanceof ComboSetting) { 
        				ComboSetting cs = (ComboSetting) s;
        				if (cs.extended) {
        					int i = 1;
        					for (String opt : cs.setting.getOptions()) {
        						//if (opt.equalsIgnoreCase(cs.setting.getValString())) continue;
        						RenderUtils.drawBorderedRect(cs.x, cs.y + 13*i, cs.x + cs.width, cs.y + cs.height + 13*i, 2, green, new Color(64, 64, 64).getRGB());
        						TextRenderUtils.drawStringCentered(opt, Minecraft.getMinecraft().fontRendererObj, cs.x + cs.width/2, cs.y + 7f + 13*i, true, 0xa0a0a0);
        						i++;
        					}
        				}
        			}
        		}
        		
        		if (tip != null) {
        			Minecraft.getMinecraft().fontRendererObj.drawString(tip, modRight + 10 + 10, modTop + 6.5f, 0xa0a0a0, true);
        		}
        	}
    	}
    	GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
	
	@Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		
		for (CategoryPanel p : panels) {
			if (p.mouseClicked(mouseX, mouseY, mouseButton)) {
				selectedPanel = p;
				p.selected = true;
			}
		}
		
    	for (CategoryPanel panel : panels) {
    		if (selectedPanel != panel) panel.selected = false;
    	}
		
		if (selectedPanel != null) {
			for (ModuleButton mb : selectedPanel.modules) {
				if (mb.mouseClicked(mouseX, mouseY, mouseButton)) {
					PitClient.moduleManager.toggle(mb.module);
				}
			}
		}
		
		if (selectedModule != null) {
			for (GuiSetting s : selectedModule.settings) {
				if (s.mouseClicked(mouseX, mouseY, mouseButton)) {
					PitClient.EVENT_BUS.post(new ClickGuiSettingEvent(s.setting));
					break;
				}
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
    }
	
	@Override
	public void keyTyped(char typedChar, int keyCode) throws IOException {

		if (selectedPanel != null) {
			for (ModuleButton mb : selectedPanel.modules) {
				if (mb.keyTyped(typedChar, keyCode)) return;
			}
		}
		if (keyCode == Keyboard.KEY_ESCAPE) open = false;
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		
		if (selectedModule != null) {
			for (GuiSetting s : selectedModule.settings) {
				s.mouseReleased(mouseX, mouseY, state);
			}
		}
		
		super.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		//System.out.println(Mouse.getEventDWheel());
	}
}
