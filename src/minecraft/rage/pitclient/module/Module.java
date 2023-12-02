package rage.pitclient.module;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import rage.pitclient.PitClient;
import rage.pitclient.settings.Setting;

public class Module {

	public Minecraft mc = Minecraft.getMinecraft();
	
	public boolean enabled;
	
	private Logger logger;
	
	private String name;

	private Category category;
	
	private int keybind;
	
	private boolean disableonvanish;
	
	private String tooltip;
	
	private Module INSTANCE;
	
	public Module() {
		this(PitClient.currentName, PitClient.currentCat, PitClient.currentKey, PitClient.currentStart, PitClient.currentTooltip);
	}
	
	public Module(String name, Category category) {
		this(name, category, 0, false, null);
	}

	public Module(String name, Category category, boolean enabledOnStart) {
		this(name, category, 0, enabledOnStart, null);
	}
	
	public Module(String name, Category category, Integer keybind) {
		this(name, category, keybind, false, null);
	}
	
	public Module(String name, Category category, Integer keybind, boolean enabledOnStart, String tooltip) {
		this.INSTANCE = this;
		this.name = name;
		this.category = category;
		this.keybind = keybind;
		this.logger = LogManager.getLogger(name);
		if (enabledOnStart) PitClient.moduleManager.setEnabled(this);
		this.settings = new ArrayList<>();
		this.tooltip = tooltip;
	}
	
	public void onEnable() {}
	public void onDisable() {}
	public void onMouseOver(float partialTicks) {}
	
	public Setting regOptionTip(String name, String tooltip, String ... options) {
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(options));
		return regSetting(new Setting(name,this,list.get(0),list,tooltip));
	}
	
	public Setting regOption(String name, String... options) {
		ArrayList<String> list = new ArrayList<String>(Arrays.asList(options));
		return regSetting(new Setting(name,this,list.get(0),list,null));
	}
	
	public Setting regBooleanTip(String name, boolean bval, String tooltip) {
		return regSetting(new Setting(name,this,bval,tooltip));
	}
	
	public Setting regBoolean(String name, boolean bval) {
		return regSetting(new Setting(name,this,bval,null));
	}
	
	public Setting regSliderTip(String name, double dval, double min, double max, boolean onlyint, String tooltip) {
		return regSetting(new Setting(name,this,dval,min,max,onlyint,tooltip));
	}
	
	public Setting regSlider(String name, double dval, double min, double max, boolean onlyint) {
		return regSetting(new Setting(name,this,dval,min,max,onlyint,null));
	}
	
	public Setting regSetting(Setting setting) {
		this.settings.add(setting);
		PitClient.settingsManager.rSetting(setting);
		return setting;
	}
	
	public ArrayList<Setting> settings;
	
	public Setting getInternalSetting(String name) {
		for (Setting s : PitClient.settingsManager.getSettingsByMod(INSTANCE)) {
			if (s.getName().equalsIgnoreCase(name)) return s;
		}
		return null;
	}
	
	public Setting getNoSpaceSetting(String name) {
		for (Setting s : PitClient.settingsManager.getSettingsByMod(INSTANCE)) {
			if (s.getName().replace(" ", "").equalsIgnoreCase(name)) return s;
		}
		return null;
	}
	
	public Setting getExternalSetting(String name) {
		return PitClient.settingsManager.getSettingByName(name);
	}
	
	public void debug(String info) {
		logger.info(info);
	}
	
	public void toggle() {
		PitClient.moduleManager.toggle(this);
	}
	
	public void setEnabled(boolean bool) {
		enabled = bool;
		if (bool) {
			onEnable();
		} else {
			onDisable();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public int getKeybind() {
		return keybind;
	}
	
	public void setKeybind(int key) {
		this.keybind = key;
	}
	
	public Category getCategory() {
		return category;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTooltip() {
		return tooltip;
	}
	
	public boolean getDisableOnVanish() {
		return disableonvanish;
	}
	
	public void setDisableOnVanish(boolean set) {
		disableonvanish = set;
	}
}
