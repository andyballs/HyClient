package rage.pitclient.module.modules.combat;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.RenderGameOverlayEvent;
import rage.pitclient.events.RenderGameOverlayEvent.ElementType;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.ReachUtils;

@ModuleInfo(name = "Reach", category = Category.COMBAT)
public class Reach extends Module {

	private Setting reach = regSlider("Reach", 3, 3, 4.5, false);
	private Setting crosshairColor = regBooleanTip("Crosshair Color", false, "Changes crosshair red/green whether or not you can hit an entity");
	private Setting reachDisplay = regBooleanTip("Reach Display", true, "Displays distance to currently selected entity");
	
	@Override
	public void onMouseOver(float partialTicks) {
		ReachUtils.distanceToEntity = 0;
		double extend = 0;
		if (PitClient.moduleManager.getModuleByName("Hitboxes").isEnabled()) {
			extend = getExternalSetting("Hitbox Extend").getValDouble();
		}
		MovingObjectPosition object = ReachUtils.getMouseOver(reach.getValDouble(), extend);

		if (object != null) {
			if (mc.objectMouseOver == null | mc.objectMouseOver.typeOfHit == MovingObjectType.MISS) {
				mc.objectMouseOver = object;
			}
		}
	}

	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
		if (!enabled)
			return;
		if (event.type == ElementType.CROSSHAIRS && crosshairColor.getValBoolean()) {
			
	        ScaledResolution scaledresolution = new ScaledResolution(mc);
	        int i = scaledresolution.getScaledWidth();
	        int j = scaledresolution.getScaledHeight();
	        GlStateManager.color(1.0F, 0.0F, 0.0F, 1.0F);
	        
	        if (ReachUtils.distanceToEntity != 0.0 && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
	        	GlStateManager.color(0.0F, 1.0F, 0.0F, 1.0F);
	        }
	        mc.getTextureManager().bindTexture(Gui.icons);
	        GlStateManager.enableBlend();
	        
	        GlStateManager.tryBlendFuncSeparate(775, 768, 1, 0);
	        GlStateManager.enableAlpha();
	        mc.ingameGUI.drawTexturedModalRect(i / 2 - 7, j / 2 - 7, 0, 0, 16, 16);
	        
	        GlStateManager.disableAlpha();
	        GlStateManager.disableBlend();
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        event.setCanceled(true);
		}
		
		if (event.type == ElementType.TEXT && reachDisplay.getValBoolean()) {
			if (ReachUtils.distanceToEntity != 0.0 && mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
		        ScaledResolution scaledresolution = new ScaledResolution(mc);
		        int i = scaledresolution.getScaledWidth();
		        int j = scaledresolution.getScaledHeight();
		        
		        double val = ReachUtils.distanceToEntity;
		        val = val*100;
		        val = (double)((int) val);
		        val = val /100;
				Double reach = getInternalSetting("Reach").getValDouble();
				if (PitClient.moduleManager.getModuleByName("Hitboxes").isEnabled()) reach += getExternalSetting("Hitbox Extend").getValDouble();
				String current = Double.toString(val);
				val = reach;
		        val = val*100;
		        val = (double)((int) val);
		        val = val /100;
				String max = Double.toString(val);
				int size = mc.fontRendererObj.getStringWidth("Reach: " + current + "/" + max);
				mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GRAY + "Reach: " + EnumChatFormatting.GOLD + current + "/" + max, i/2 - size/2, j - 70, 0);
				
			}
		}
	}

}
