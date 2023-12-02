package rage.pitclient.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.clickgui.ClickGUIScreen;
import rage.pitclient.clickgui.Panel;
import rage.pitclient.clickgui.elements.Element;
import rage.pitclient.clickgui.elements.ModuleButton;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.packets.PacketEvent;
import rage.pitclient.settings.Setting;

public class ConfigManager {

	public ConfigManager() {
		load();
	}
	
	private Config config;
	private String configName = "default.json";
	
	public void save() {
		File configFile = new File(PitClient.getInstance().pitModConfigManager.configDir+"\\configs\\",configName);
		(config = new Config(configFile)).load(true);
		for (Panel p : ClickGUIScreen.panels) {
			JsonObject object = new JsonObject();
			object.addProperty("x", Double.valueOf(p.x));
			object.addProperty("y", Double.valueOf(p.y));
			object.addProperty("extended", Boolean.valueOf(p.extended));
			config.set(p.title.toUpperCase(), object);
			for (ModuleButton mb : p.Elements) {
				object = new JsonObject();
				object.addProperty("enabled", Boolean.valueOf(mb.mod.isEnabled()));
				object.addProperty("bind", Integer.valueOf(mb.mod.getKeybind()));
				object.addProperty("disableonvanish", Boolean.valueOf(mb.mod.getDisableOnVanish()));
				for (Element e : mb.menuelements) {
					if (e.set.isCheck()) {
						object.addProperty(e.set.getName(), Boolean.valueOf(e.set.getValBoolean()));
					}
					if (e.set.isCombo()) {
						object.addProperty(e.set.getName(), String.valueOf(e.set.getValString()));
					}
					if (e.set.isSlider()) {
						object.addProperty(e.set.getName(), Double.valueOf(e.set.getValDouble()));
					}
				}
				config.set(mb.mod.getName(), object);
			}
		}
		config.save();
	}
	
	public void load() {
		
		try {
		
			File configFile = new File(PitClient.getInstance().pitModConfigManager.configDir+"\\configs\\",configName);
	
			(config = new Config(configFile)).load(false);
			if (config.getObject() == null) return;
			for (Entry<String, JsonElement> entry : config.getObject().entrySet()) {
				String name = entry.getKey();
				
				Module mod = PitClient.moduleManager.getModuleByName(name);
				
				if (mod == null) {
					Category category = Category.valueOf(name.toUpperCase());
					 Panel panel = PitClient.clickGuiScreen.getPanelByName(category.name());
					 if (panel == null) continue;
					 
					 JsonObject value = entry.getValue().getAsJsonObject();
					 boolean extended = value.get("extended").getAsBoolean();
					 int x = value.get("x").getAsInt();
					 int y = value.get("y").getAsInt();
					 panel.extended = extended;
					 panel.x = x;
					 panel.y = y;
					 continue;
				}
				
				JsonObject value = entry.getValue().getAsJsonObject();
				
				if (value.get("enabled").getAsBoolean()) {
					PitClient.moduleManager.setEnabled(PitClient.moduleManager.getModuleByName(entry.getKey()));
				} else PitClient.moduleManager.setDisabled(PitClient.moduleManager.getModuleByName(entry.getKey()));
				
				PitClient.moduleManager.getModuleByName(entry.getKey()).setDisableOnVanish(value.get("disableonvanish").getAsBoolean());;
	
				int bind = value.get("bind").getAsInt();	
				mod.setKeybind(bind);
				
				for (Entry<String, JsonElement> element : value.entrySet()) {
					String settingName = element.getKey();
					if (settingName.equals("enabled") | settingName.equals("bind") | settingName.equals("disableonvanish")) continue;
					
					Setting setting = mod.getInternalSetting(settingName);
					if (setting == null) continue;
					JsonElement val = element.getValue();
					if (setting.isCheck()) {
						setting.setValBoolean(val.getAsBoolean());
					}
					if (setting.isCombo()) {
						setting.setValString(val.getAsString());
					}
					if (setting.isSlider()) {
						setting.setValDouble(val.getAsDouble());
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error Loading Config " + configName);
		}
	}
	
	private boolean listening;
	private String message;
	
	@SubscribeEvent
	public void onMessage(PacketEvent.Outgoing.Pre event) {
		if (event.getPacket() instanceof C14PacketTabComplete) {
			C14PacketTabComplete packet = (C14PacketTabComplete) event.getPacket();
			if (packet.getMessage().startsWith(".")) {
				listening = true;
				message = packet.getMessage();
			}
		}
		if (event.getPacket() instanceof C01PacketChatMessage) {
			C01PacketChatMessage packet = (C01PacketChatMessage) event.getPacket();
			if (packet.getMessage().startsWith(".")) {
				event.setCanceled(true);
				handleMessage(packet.getMessage());
			}
		}
	}
	
	private void configMessage(String[] message) {
		
		if (message.length != 3) {
			PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + "Invalid config command");
			return;
		}
		
		String command = message[1];
		String name = message[2];
		
		if (command.equalsIgnoreCase("load")) {
			if (!new File(PitClient.getInstance().pitModConfigManager.configDir+"\\configs\\",name + ".json").exists()) {
				PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + "Config " + name + " not found");
				return;
			}
			configName = name + ".json";
			load();
			PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.GREEN + "Config " + EnumChatFormatting.GOLD + name + EnumChatFormatting.GREEN +  " loaded");
			return;
		}
		if (command.equalsIgnoreCase("save")) {
			configName = name + ".json";
			save();
			PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.GREEN + "Config " + EnumChatFormatting.GOLD + name + EnumChatFormatting.GREEN +  " saved");
			return;
		}
		PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + "Invalid config command");
	}
	
	private void handleMessage(String message) {
		
		String[] split = message.substring(1).split(" ");
		
		if (split[0].equalsIgnoreCase("f") | split[0].equalsIgnoreCase("e")) return;
		
		if (split[0].equalsIgnoreCase("c") | split[0].equalsIgnoreCase("config")) {
			configMessage(split);
			return;
		}
		
		Module mod = PitClient.moduleManager.getNoSpaceMod(split[0]);
		
		if (mod == null) {
			PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + "Module " + split[0] + " not found");
			return;
		}
		
		if (split.length == 1) {
			PitClient.moduleManager.swap(mod);
			return;
		}
		
		Setting set = mod.getNoSpaceSetting(split[1]);
		if (set == null) {
			PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + "Setting " + split[1] + " not found");
			return;
		}
		
		if (split.length == 2) {
			if (set.isCheck()) {
				set.setValBoolean(!set.getValBoolean());
				String first = EnumChatFormatting.GOLD + set.getName() + EnumChatFormatting.AQUA + " has been ";
				String msg = set.getValBoolean() ? EnumChatFormatting.GREEN + "enabled" :EnumChatFormatting.RED + "disabled";
				PitClient.commandManager.sendModuleMessage(mod, first + msg);
				return;
			}
			if (set.isCombo()) {
				String msg = EnumChatFormatting.GOLD + set.getName() + EnumChatFormatting.AQUA + " is set to " + EnumChatFormatting.GOLD + set.getValString();
				PitClient.commandManager.sendModuleMessage(mod, msg);
				return;
			}
			if (set.isSlider()) {
				String msg = EnumChatFormatting.GOLD + set.getName() + EnumChatFormatting.AQUA + " is set to " + EnumChatFormatting.GOLD + set.getValDouble();
				PitClient.commandManager.sendModuleMessage(mod, msg);
				return;
			}
		}
		
		if (split.length == 3) {
			if (set.isCombo()) {
				String option = null;
				for (String o : set.getOptions()) {
					if (o.replace(" ", "").equalsIgnoreCase(split[2])) option = o;
				}
				if (option == null) {
					PitClient.commandManager.sendModuleMessage(mod,EnumChatFormatting.RED + "Option " + split[2] + " in " + set.getName() + " not found");
					return;
				}
				set.setValString(option);
				String msg = EnumChatFormatting.GOLD + set.getName() + EnumChatFormatting.AQUA + " has been set to " + EnumChatFormatting.GOLD + option;
				PitClient.commandManager.sendModuleMessage(mod, msg);
				return;
			}
			
			if (set.isSlider()) {
				double min = set.getMin();
				double max = set.getMax();
				double val;
				try {
					val = Double.parseDouble(split[2]);
				} catch(Exception e) {
					PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + split[2] + " Is not a number");
					return;
				}
				
				if (max >= val && min <= val) {
					set.setValDouble(val);
					String msg = EnumChatFormatting.GOLD + set.getName() + EnumChatFormatting.AQUA + " has been set to " + EnumChatFormatting.GOLD + val;
					PitClient.commandManager.sendModuleMessage(mod, msg);
					return;
				} else {
					PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + split[2] + " Is not between " + min + " - " + max);
					return;
				}
			}
			
			if (set.isCheck()) {
				PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + "Use '."+mod.getName().replace(" ", "") + " " + set.getName().replace(" ", "") + "' to toggle");
				return;
			}
		}
		
		PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.RED + "Too many arguments");
		return;
	}
	
	private void tabComplete(String message) {
		ArrayList<String> list = new ArrayList<String>();
		
		if (message.equals(".")) {
			list.add(".f");
			list.add(".e");
			for (Module m : PitClient.moduleManager.getModules()) list.add("."+m.getName().replace(" ", ""));
		}
		if (message.equalsIgnoreCase(".f ") | message.equalsIgnoreCase(".e ")) {
			list.add("remove");
			list.add("add");
			list.add("clear");
		}
		if ((message.startsWith(".e ") | message.startsWith(".f ")) && (message.endsWith("add ") | message.endsWith("remove "))) {
			for (NetworkPlayerInfo p : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
				list.add(p.getGameProfile().getName());
			}
		}
		
		String[] ret = new String[list.size()];
		int i = 0;
		for (String s : list) ret[i++] = s;

		Minecraft.getMinecraft().getNetHandler().handleTabComplete(new S3APacketTabComplete(ret));
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (!listening)
			return;
		listening = false;
		tabComplete(message);
	}
}
