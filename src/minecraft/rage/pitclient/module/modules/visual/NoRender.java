package rage.pitclient.module.modules.visual;

import net.minecraft.entity.EntityLivingBase;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.RenderLivingEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "No Render", tooltip = "Cancels rendering options to save fps", category = Category.VISUAL)
public class NoRender extends Module {

	private Setting renderEntities = regBooleanTip("Entities", true, "Don't render entities");
	private Setting maxDistance = regSliderTip("Entity Render Distance", 256, 0, 512, true, "Distance to render entities at");
	
	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Pre<EntityLivingBase> event) {
		if (!enabled)
			return;
		
		mc.gameSettings.field_181151_V = true;
		
		if (event.entity.getDistanceToEntity(mc.thePlayer) > maxDistance.getValDouble()) {
			event.setCanceled(true);
			mc.gameSettings.field_181151_V = false;
			return;
		}
		
		if (renderEntities.getValBoolean()) {
			event.setCanceled(true);
			mc.gameSettings.field_181151_V = false;
			return;
		}		
	}
	
	@Override
	public void onDisable() {
		mc.gameSettings.field_181151_V = true;
	}

}
