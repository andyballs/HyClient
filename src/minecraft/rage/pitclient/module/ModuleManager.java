package rage.pitclient.module;

import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.events.TickEvent.Phase;

public class ModuleManager {
	
	private ArrayList<Module> modules;
	
	private ArrayList<Module> enabledModules;

	
	public ModuleManager() {
		modules = new ArrayList<>();
		enabledModules = new ArrayList<>();
	}
	
	public void register(Module module) {
		modules.add(module);
		PitClient.EVENT_BUS.register(module);
	}
	
	public ArrayList<Module> getModules() {
		return modules;
	}
	
	public ArrayList<Module> getEnabledModules() {
		return enabledModules;
	}
	
	public void setEnabled(Module module) {
		enabledModules.remove(module);
		enabledModules.add(module);	
		module.setEnabled(true);
	}
	
	public void setDisabled(Module module) {
		enabledModules.remove(module);
		module.setEnabled(false);
	}
	
	public void toggle(Module module) {
		if (!enabledModules.remove(module))
			enabledModules.add(module);
		module.setEnabled(!module.isEnabled());
	}
	
	public Module getModuleByClass(Class<?> clazz) {
		for (Module mod : modules) {
			if (clazz.getName().equals(mod.getClass().getName())) {
				return mod;
			}
		}
		return null;
	}
	
	public Module getNoSpaceMod(String name) {
		for (Module mod : modules) {
			if (mod.getName().replace(" ", "").equalsIgnoreCase(name)) {
				return mod;
			}
		}
		return null;
	}
	
	public Module getModuleByName(String name) {
		for (Module mod : modules) {
			if (mod.getName().equalsIgnoreCase(name)) {
				return mod;
			}
		}
		return null;
	}
	
	private HashMap<Module,Integer> tickList = new HashMap<>();
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (Minecraft.getMinecraft().theWorld == null)
			return;
		if (event.phase != Phase.END)
			return;
		modules.forEach(module -> {
			if (Keyboard.isKeyDown(module.getKeybind())) {
				int curr = tickList.get(module);
				if (curr == 0 && Minecraft.getMinecraft().currentScreen == null) {
					swap(module);
				}
				tickList.put(module, curr + 1);
			} else tickList.put(module, 0);
		});
	}
	
	public void swap(Module module) {
		toggle(module);
		
		if (module.getCategory() != Category.INVISIBLE && !module.getName().equals("Click Gui")) {
			String enableMsg = module.isEnabled() ? EnumChatFormatting.GREEN + "enabled" : EnumChatFormatting.RED + "disabled";
			PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.GOLD + module.getName() + EnumChatFormatting.AQUA + " has been " + enableMsg);
		}
	}
}
