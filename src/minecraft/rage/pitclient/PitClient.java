package rage.pitclient;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import rage.pitclient.clickgui.ClickGUIScreen;
import rage.pitclient.clickgui.util.FontUtil;
import rage.pitclient.commands.CommandManager;
import rage.pitclient.config.ConfigManager;
import rage.pitclient.config.PitModConfigManager;
import rage.pitclient.eventbus.EventBus;
import rage.pitclient.freelook.FreeLook;
import rage.pitclient.listener.EventListener;
import rage.pitclient.login.screen.GuiSessionManager;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.module.ModuleManager;
import rage.pitclient.module.modules.combat.VenomSwapper;
import rage.pitclient.newgui.NewGui;
import rage.pitclient.settings.SettingsManager;
import ru.mdashlw.hypixel.pit.stats.HypixelPitStats;

public class PitClient {
	public static final String commandPrefix = EnumChatFormatting.WHITE + "[" + EnumChatFormatting.RED + "Pit"
			+ EnumChatFormatting.GOLD + "Mod" + EnumChatFormatting.WHITE + "] ";
	public static final String MODID = "pitmod";
	public static final String VERSION = "1.0-REL";

	private static final Logger logger = LogManager.getLogger("PitMod");

	private static PitClient INSTANCE;

	private static PitModManager manager;

	private static HypixelApi hypixelApi;
	public GuiScreen guiToOpen;

	public static ModuleManager moduleManager;
	public static CommandManager commandManager;
	public static ScoreboardManager scoreboardManager;
	public static SettingsManager settingsManager;
	public static ClickGUIScreen clickGuiScreen;
	public static NewGui newGui;
	public static PitModConfigManager pitModConfigManager;
	public static ConfigManager configManager;
	public static FriendEnemyManager friendManager;
	public static GuiSessionManager guiSessionManager;
	
	public static boolean useNewGui = true;
	
	public static FreeLook freeLook;
	
	public static final EventBus EVENT_BUS = new EventBus();
	
	public static void intialize() {
		setHighestPerms();
		INSTANCE = new PitClient();		
		pitModConfigManager = new PitModConfigManager();
		manager = new PitModManager();
		hypixelApi = new HypixelApi();
		commandManager = new CommandManager();
		settingsManager = new SettingsManager();
		guiSessionManager = new GuiSessionManager();
		scoreboardManager = new ScoreboardManager();
		moduleManager = new ModuleManager();
		friendManager = new FriendEnemyManager();
		new HypixelPitStats().initialize();
		
		EVENT_BUS.register(freeLook = new FreeLook());
		
		EVENT_BUS.register(scoreboardManager);
		EVENT_BUS.register(moduleManager);
		EVENT_BUS.register(friendManager);
		
		Minecraft.getMinecraft().gameSettings.viewBobbing = false;
		Minecraft.getMinecraft().gameSettings.ofFogType = 3;

		EVENT_BUS.register(new EventListener());

		VenomSwapper.pantsSwap = new KeyBinding("Swap Pants Macro", Keyboard.KEY_NONE, "PitMod");
		registerKeyBinding(VenomSwapper.pantsSwap);
		VenomSwapper.bootSwap = new KeyBinding("Swap Boots Macro", Keyboard.KEY_NONE, "PitMod");
		registerKeyBinding(VenomSwapper.bootSwap);
		
		FontUtil.setupFontUtils();
		registerModules();
		clickGuiScreen = new ClickGUIScreen();
		newGui = new NewGui();
		EVENT_BUS.register(configManager = new ConfigManager());
	}
	
    public static void registerKeyBinding(KeyBinding key)
    {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.add(Minecraft.getMinecraft().gameSettings.keyBindings, key);
    }
    
    //I KNOW THIS IS SHIT
    public static String currentName;
    public static Category currentCat;
    public static int currentKey;
    public static boolean currentStart;
    public static String currentTooltip;
    
	private static void registerModules() {
		
		try {
			search = "rage.pitclient.module.modules";
			
			for (ClassInfo classInfo : ClassPath.from(PitClient.class.getClassLoader()).getTopLevelClasses()) {
				
				if (!classInfo.getName().contains(search)) continue;
				
				try {
					Class<?> modClass = Class.forName(classInfo.getName(), false, PitClient.class.getClassLoader());
					
					
					if (!modClass.isAnnotationPresent(ModuleInfo.class)) continue;		
					if (modClass.getSuperclass() == null | !modClass.getSuperclass().isAssignableFrom(Module.class)) continue;
					
					Constructor<?> cons = modClass.getConstructor();
					
					ModuleInfo modInfo = (ModuleInfo) modClass.getAnnotation(ModuleInfo.class);
					
					currentName = modInfo.name();
					currentKey = modInfo.key();
					currentStart = modInfo.defaultEnabled();
					currentTooltip = modInfo.tooltip() == "" ? null : modInfo.tooltip();
					currentCat = modInfo.category();
					
					int permissionRequired = modInfo.permission();
					if (permissionRequired > clientUser.getHighestPerm()) continue;
					
					Module module = (Module) modClass.cast(cons.newInstance());
					
					moduleManager.register(module);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private static ClientUser clientUser;
	
	public static ClientUser getClientUser() {
		return clientUser;
	}
	
	private static void setHighestPerms() {
		String data = "{\"_id\":-1,\"username\":\"PublicUser\",\"permissions\":[\"OWNER\"],\"hwid\":\"0\",\"discordid\":\"0\"}";
		JsonObject vals = (JsonObject) new JsonParser().parse(data);
		setClientUserData(vals);
	}

	public static void setClientUserData(JsonObject data) {
		JsonArray perms = data.get("permissions").getAsJsonArray();
		String username = data.get("username").getAsString();
		int uid = data.get("_id").getAsInt();
		String hwid = data.get("hwid").getAsString();
		String discord = data.get("discordid").getAsString();
		
		clientUser = new ClientUser(username, uid, discord, perms, hwid);
	}
	
	public static void setSession(String username, String session) {
		String uuid = null;
		if (username.length() <= 16) {
			uuid = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(username).getId().toString();
		} 
		if (username.length() == 32) {
			BigInteger bi1 = new BigInteger(username.substring(0, 16), 16);                
			BigInteger bi2 = new BigInteger(username.substring(16, 32), 16);
			UUID Uuid = new UUID(bi1.longValue(), bi2.longValue());
			username = Uuid.toString();
		}
		if (username.length() == 36) {
			uuid = username;
			GameProfile possible = MinecraftServer.getServer().getPlayerProfileCache().getProfileByUUID(UUID.fromString(uuid));
			if (possible != null) {
				username = possible.getName();
			}
		}
		
		System.out.println(username);
		System.out.println(uuid);
		System.out.println(session);
		
    	Session newSession = new Session(username, uuid, session, "mojang");
    	
    	Minecraft.getMinecraft().session = newSession;
	}

	public HypixelApi getHypixelApi() {
		return hypixelApi;
	}

	public static PitClient getInstance() {
		return INSTANCE;
	}

	public static Logger getLogger() {
		return logger;
	}

	public PitModManager getManager() {
		return manager;
	}
}