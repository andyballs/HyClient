package rage.pitclient.clickgui.util;

import java.awt.Color;

import rage.pitclient.PitClient;

public class ColorUtil {
	
	public static Color getClickGUIColor(){
		return new Color((int)PitClient.settingsManager.getSettingByName("GuiRed").getValDouble(), (int)PitClient.settingsManager.getSettingByName("GuiGreen").getValDouble(), (int)PitClient.settingsManager.getSettingByName("GuiBlue").getValDouble());
	}
}
