package rage.pitclient.module.modules.combat;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.ReachUtils;

@ModuleInfo(name = "Hitboxes", tooltip = "Extends entity hitboxes", category = Category.COMBAT)
public class Hitboxes extends Module {

	private Setting extend = regSlider("Hitbox Extend", 0, 0, 1.6, false);
	private Setting throughWalls = regBooleanTip("Through Walls", true, "Can you hit through walls using the extended hitbox");

	@Override
	public void onMouseOver(float partialTicks) {
		MovingObjectPosition object = ReachUtils.getMouseOver(3.0D, extend.getValDouble());
		if (object != null) {
			if (ReachUtils.distanceToEntity <= extend.getValDouble()) {
				if (throughWalls.getValBoolean() && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
					mc.objectMouseOver = object;
				}
			}
		}
	}
}
