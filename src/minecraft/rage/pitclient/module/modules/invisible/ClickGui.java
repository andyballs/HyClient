package rage.pitclient.module.modules.invisible;

import org.lwjgl.input.Keyboard;

import rage.pitclient.PitClient;
import rage.pitclient.clickgui.ClickGUIScreen;
import rage.pitclient.clickgui.util.FontUtil;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.newgui.NewGui;

@ModuleInfo(name = "Click Gui", key = Keyboard.KEY_GRAVE, category = Category.INVISIBLE)
public class ClickGui extends Module {
	
	@Override
	public void onEnable() {
		PitClient.moduleManager.toggle(this);
		FontUtil.setupFontUtils();
		if (PitClient.useNewGui) {
			if (!(mc.currentScreen instanceof NewGui)) {
				mc.displayGuiScreen(PitClient.newGui);
			}
		} else {
			if (!(mc.currentScreen instanceof ClickGUIScreen)) {
				mc.displayGuiScreen(PitClient.clickGuiScreen);
			}
		}


	}

}
