package rage.pitclient.module.modules.visual;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import rage.pitclient.PitClient;
import rage.pitclient.clickgui.util.FontUtil;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.RenderGameOverlayEvent;
import rage.pitclient.events.RenderGameOverlayEvent.ElementType;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Module List", category = Category.VISUAL)
public class ModuleList extends Module {

	private Setting sortingOption = regOptionTip("Sorting", "Determines how the list is sorted", "Length", "Length Reversed", "Category");	
	private Setting fontRendererOption = regOption("Font", FontUtil.getFontRendererMap().keySet().toArray(new String[0]));
	private Setting fontScale = regSlider("Font Scale", 1, 0.1, 2, false);
	
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Post event) {
		if (!enabled)
			return;
		if (event.type != ElementType.TEXT)
			return;
		if (mc.theWorld == null)
			return;
		
		FontRenderer fr = FontUtil.getFontRendererByName(fontRendererOption.getValString());
		
		ScaledResolution res = new ScaledResolution(mc);
		
		Map<String, Module> moduleList = new HashMap<>();
		ArrayList<String> nameList = new ArrayList<String>();
		
		PitClient.moduleManager.getEnabledModules().forEach(module -> {
			if (module.getCategory() != Category.INVISIBLE) {
				moduleList.put(module.getName(), module);
				nameList.add(module.getName());
			}
		});

		nameList.sort(Comparator.comparingInt(fr::getStringWidth).reversed());
		String sort = sortingOption.getValString();
		if (sort.equalsIgnoreCase("length")) nameList.sort(Comparator.comparingInt(fr::getStringWidth).reversed());
		if (sort.equalsIgnoreCase("category")) nameList.sort(Comparator.comparingInt(this::categorySort));
		if (sort.equalsIgnoreCase("length reversed")) nameList.sort(Comparator.comparingInt(fr::getStringWidth));
		
		for (int i = 0; i < nameList.size(); i++) {
			String name = nameList.get(i);
			Module module = moduleList.get(name);
			
			double scale = fontScale.getValDouble();
			
			int width = (int) (fr.getStringWidth(name) * scale);
			int xpos = res.getScaledWidth() - 2 - width;
			int ypos = (int) (2 + 9 * i * scale);
			int color = getColor(module);
			
			GlStateManager.scale(scale, scale, 1.0F);
			fr.drawString(name, (int)(xpos / scale),(int) (ypos / scale), color);
			GlStateManager.scale(1.0F / scale, 1.0F / scale, 1.0F);
		}
		mc.getTextureManager().bindTexture(Gui.icons);
		//TextureUtil.glGenTextures();
	}
	
	private int categorySort(String modName) {
		return PitClient.moduleManager.getModuleByName(modName).getCategory().ordinal();
	}
	
	private int getColor(Module module) {
		
		switch (module.getCategory()) {
		case COMBAT:
			return 16711680;
		case OTHER:
			return 16711842;
		case VISUAL:
			return 65468;
		case MOVEMENT:
			return 16774912;
		default:
			return 16774912;		
		}
		
	}

}
