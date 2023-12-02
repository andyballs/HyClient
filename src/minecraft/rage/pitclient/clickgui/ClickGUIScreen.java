package rage.pitclient.clickgui;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import rage.pitclient.PitClient;
import rage.pitclient.clickgui.elements.Element;
import rage.pitclient.clickgui.elements.ModuleButton;
import rage.pitclient.clickgui.elements.menu.ElementSlider;
import rage.pitclient.clickgui.util.ColorUtil;
import rage.pitclient.clickgui.util.FontUtil;
import rage.pitclient.clickgui.util.GuiFix;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.modules.invisible.ClickGui;
import rage.pitclient.settings.SettingsManager;

public class ClickGUIScreen extends GuiScreen {
	public static ArrayList<Panel> panels;
	public static ArrayList<Panel> rpanels;
	public ModuleButton mb = null;
	public SettingsManager setmgr;

	public ClickGUIScreen() {
		setmgr = PitClient.settingsManager;
		FontUtil.setupFontUtils();
		panels = new ArrayList<>();
		double pwidth = 80;
		double pheight = 15;
		double px = 10;
		double py = 10;
		double pyplus = pheight + 10;
		
		for (Category c : Category.values()) {
			if (c == Category.INVISIBLE) continue;
			String title = Character.toUpperCase(c.name().toLowerCase().charAt(0)) + c.name().toLowerCase().substring(1);
			panels.add(new Panel(title, px, py, pwidth, pheight, false, this) {
						@Override
						public void setup() {
							for (Module m : PitClient.moduleManager.getModules()) {
								if (!m.getCategory().equals(c)) continue;
								this.Elements.add(new ModuleButton(m, this) {
									@Override
									public boolean keyTyped(char typedChar, int keyCode) throws IOException {
										if (!mod.getName().contains("Config")) return super.keyTyped(typedChar, keyCode);
										return false;
									}
								});
							}
						}
			});
			py += pyplus;
		}
		
		rpanels = new ArrayList<Panel>();
		for (Panel p : panels) {
			rpanels.add(p);
		}
		Collections.reverse(rpanels);

	}
	
	private HashMap<String, Integer> tooltipMap = new HashMap<>();

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		for (Panel p : panels) {
			p.drawScreen(mouseX, mouseY, partialTicks);
		}

		ScaledResolution s = new ScaledResolution(mc);
		
		mb = null;

		listen:
		for (Panel p : panels) {
			if (p != null && p.visible && p.extended && p.Elements != null
					&& p.Elements.size() > 0) {
				for (ModuleButton e : p.Elements) {
					if (e.listening) {
						mb = e;
						break listen;
					}
				}
			}
		}
		
		String tooltip = null;
		
		
		for (Panel panel : panels) {
			if (panel.extended && panel.visible && panel.Elements != null) {
				for (ModuleButton b : panel.Elements) {
					if (b.extended && b.menuelements != null && !b.menuelements.isEmpty()) {
						double off = 0;
						Color temp = ColorUtil.getClickGUIColor().darker();
						int outlineColor = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), 170).getRGB();
						
						for (Element e : b.menuelements) {
							e.offset = off;
							e.update();
							if (PitClient.settingsManager.getSettingByName("Design").getValString().equalsIgnoreCase("New")){
								GuiFix.drawRect(e.x, e.y, e.x + e.width + 2, e.y + e.height, outlineColor);
							}
							e.drawScreen(mouseX, mouseY, partialTicks);
							off += e.height;
							if (e.isHovered(mouseX, mouseY)) {
								tooltip = e.set.getTooltip();
								if (!tooltipMap.containsKey(tooltip))
									tooltipMap.put(tooltip, 0);
								
								tooltipMap.replace(tooltip, tooltipMap.get(tooltip)+1);
							} else tooltipMap.put(e.set.getTooltip(), 0);
						}
					}
					if (b.isHovered(mouseX, mouseY)) {
						tooltip = b.mod.getTooltip();
						if (!tooltipMap.containsKey(tooltip))
							tooltipMap.put(tooltip, 0);
						
						tooltipMap.replace(tooltip, tooltipMap.get(tooltip)+1);
					} else tooltipMap.put(b.mod.getTooltip(), 0);
				}
			}

		}
		
		if (tooltip != null) {
			if (tooltipMap.get(tooltip) > 100) {
				GuiFix.drawRect(mouseX + 10, mouseY + 10, mouseX + FontUtil.getStringWidth(tooltip) + 13, mouseY + FontUtil.getFontHeight() + 13, 0xd9111111);
				
				float factor = 1f;
				//GlStateManager.scale(factor, factor, 1.0F);
				FontUtil.drawStringWithShadow(tooltip, (mouseX + 11) / factor, (mouseY + 11) / factor, 0xffefefef);
				//GlStateManager.scale(1.0F / factor, 1.0F / factor, 1.0F);
			}
		}
		
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

		for (Panel panel : rpanels) {
			if (mb == null && panel.extended && panel.visible && panel.Elements != null) {
				for (ModuleButton b : panel.Elements) {
					if (b.extended) {
						for (Element e : b.menuelements) {
							if (e.mouseClicked(mouseX, mouseY, mouseButton))
								return;
						}
					}
				}
			}
		}

		for (Panel p : rpanels) {
			if (p.mouseClicked(mouseX, mouseY, mouseButton))
				return;
		}
		
		if (mb != null) return;
		
		try {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int state) {
		if(mb != null)return;
		
		for (Panel panel : rpanels) {
			if (panel.extended && panel.visible && panel.Elements != null) {
				for (ModuleButton b : panel.Elements) {
					if (b.extended) {
						for (Element e : b.menuelements) {
							e.mouseReleased(mouseX, mouseY, state);
						}
					}
				}
			}
		}
		
		for (Panel p : rpanels) {
			p.mouseReleased(mouseX, mouseY, state);
		}
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		for (Panel p : rpanels) {
			if (p != null && p.visible && p.extended && p.Elements != null && p.Elements.size() > 0) {
				for (ModuleButton e : p.Elements) {
					try {
						if (e.keyTyped(typedChar, keyCode))return;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}

		try {
			if (keyCode == 1) {
				FontUtil.setupFontUtils();
				PitClient.moduleManager.getModuleByClass(ClickGui.class).setEnabled(false);
			}
			super.keyTyped(typedChar, keyCode);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	@Override
	public void initGui() {

		if (PitClient.settingsManager.getSettingByName("Blur").getValBoolean() == false) return;
		if (mc.getRenderViewEntity() instanceof EntityPlayer) {
			if (mc.entityRenderer.getShaderGroup() != null) {
				mc.entityRenderer.getShaderGroup().deleteShaderGroup();
			}
			mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
			
		}
	}

	@Override
	public void onGuiClosed() {

		if (mc.entityRenderer.getShaderGroup() != null) {
			mc.entityRenderer.stopUseShader();
		}
		
		for (Panel panel : ClickGUIScreen.rpanels) {
			if (panel.extended && panel.visible && panel.Elements != null) {
				for (ModuleButton b : panel.Elements) {
					if (b.extended) {
						for (Element e : b.menuelements) {
							if(e instanceof ElementSlider){
								((ElementSlider)e).dragging = false;
							}
						}
					}
				}
			}
		}
	}

	public void closeAllSettings() {
		for (Panel p : rpanels) {
			if (p != null && p.visible && p.extended && p.Elements != null
					&& p.Elements.size() > 0) {
				for (ModuleButton e : p.Elements) {
					e.extended = false;
				}
			}
		}
	}
	
	public Panel getPanelByName(String name) {
		for (Panel p : panels) {
			if (p.title.equalsIgnoreCase(name)) return p;
		}
		return null;
	}
}
