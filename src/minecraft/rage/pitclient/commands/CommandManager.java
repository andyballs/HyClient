package rage.pitclient.commands;

import static net.minecraft.util.EnumChatFormatting.RED;

import java.lang.reflect.Field;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.GuiOverlayEditor;
import rage.pitclient.GuiWarningList;
import rage.pitclient.PitClient;
import rage.pitclient.PitModManager;
import rage.pitclient.module.Module;

public class CommandManager extends CommandHandler {
	
	public static CommandManager instance;	
	
	public CommandManager() {
		instance = this;
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				if (field.getType().isAssignableFrom(SimpleCommand.class)) {
					SimpleCommand cmd = (SimpleCommand) field.get(this);
					this.registerCommand(cmd);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    @Override
    public int executeCommand(ICommandSender sender, String message)
    {
        message = message.trim();

        if (message.startsWith("/"))
        {
            message = message.substring(1);
        }

        String[] temp = message.split(" ");
        String[] args = new String[temp.length - 1];
        String commandName = temp[0];
        System.arraycopy(temp, 1, args, 0, args.length);
        ICommand icommand = (ICommand) getCommands().get(commandName);

        try
        {
            if (icommand == null)
            {
                return 0;
            }

            if (icommand.canCommandSenderUseCommand(sender))
            {
//                CommandEvent event = new CommandEvent(icommand, sender, args);
//                if (MinecraftForge.EVENT_BUS.post(event))
//                {
//                    if (event.exception != null)
//                    {
//                        throw event.exception;
//                    }
//                    return 0;
//                }

                icommand.processCommand(sender, args);
                return 1;
            }
            else
            {
                sender.addChatMessage(format(RED, "commands.generic.permission"));
            }
        }
        catch (WrongUsageException wue)
        {
            sender.addChatMessage(format(RED, "commands.generic.usage", format(RED, wue.getMessage(), wue.getErrorObjects())));
        }
        catch (CommandException ce)
        {
            sender.addChatMessage(format(RED, ce.getMessage(), ce.getErrorObjects()));
        }
        catch (Throwable t)
        {
            sender.addChatMessage(format(RED, "commands.generic.exception"));
            t.printStackTrace();
        }

        return -1;
    }
    
    //Couple of helpers because the mcp names are stupid and long...
    private ChatComponentTranslation format(EnumChatFormatting color, String str, Object... args)
    {
        ChatComponentTranslation ret = new ChatComponentTranslation(str, args);
        ret.getChatStyle().setColor(color);
        return ret;
    }
	
	private final static String commandPrefix = EnumChatFormatting.WHITE + "[" + EnumChatFormatting.RED + "Pit" + EnumChatFormatting.GOLD + "Mod" + EnumChatFormatting.WHITE + "] ";
	
	private PitModManager manager = PitClient.getInstance().getManager();
	
	public void sendMessageWithPrefix(String message) {
		if (Minecraft.getMinecraft().theWorld == null)
			return;
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(commandPrefix + message));
	}
	
	public void sendMessage(String message) {
		if (Minecraft.getMinecraft().theWorld == null)
			return;
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
	}
	
	public void sendModuleMessage(Module mod, String message) {
		if (Minecraft.getMinecraft().theWorld == null)
			return;
		String prefix = EnumChatFormatting.WHITE + "[" + EnumChatFormatting.RED + mod.getName() + EnumChatFormatting.WHITE + "] ";
		
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(prefix + message));
	}
	
	
	
	SimpleCommand pitModGoldCommand = new SimpleCommand("pitmodgold", new SimpleCommand.ProcessCommandRunnable() {
		public void processCommand(ICommandSender sender, String[] args) {
			float curr = manager.getCurrentPrestigeGold();
			if (curr < 0.0F) {
				sender.addChatMessage(new ChatComponentText(commandPrefix + EnumChatFormatting.RED
								+ "Pit stats has not been downloaded from API. Are you sure the api key is set?"));

				return;
			}

			float max = manager.getCurrentPrestigeGoldMax();
			if (max == 0.0F) {
				sender.addChatMessage(new ChatComponentText(commandPrefix
						+ EnumChatFormatting.GOLD + "Pres 35 has a gold requirement? You must be from the future o.O"));

			} else if (max < 0.0F) {
				sender.addChatMessage(new ChatComponentText(
						commandPrefix + EnumChatFormatting.GOLD + "Gold: " + (int) curr));
			} else {

				float perc = (float) Math.ceil((curr / max * 1000.0F)) / 10.0F;
				if (perc > 100.0F)
					perc = 100.0F;
				sender.addChatMessage(new ChatComponentText(commandPrefix
						+ EnumChatFormatting.GOLD + "Gold: " + (int) curr + "/" + (int) max + " (" + perc + "%)"));
			}
		}
	});

	SimpleCommand pitModCommand = new SimpleCommand("pitmod", new SimpleCommand.ProcessCommandRunnable() {
		public void processCommand(ICommandSender sender, String[] args) {
			if (args.length == 0) {
				PitClient.getInstance().guiToOpen = new GuiOverlayEditor(PitClient.getInstance().pitModConfigManager.getOverlays());
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("help")) {
					sender.addChatMessage(new ChatComponentText(
							commandPrefix + EnumChatFormatting.GREEN + "Help"));

					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + 
							"/pitmod - Edit the overlay"));

					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + 
							"/pitmod help - Displays this message"));

					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + 
							"/pitmodwarning - Add warnings for when certain players leave/join"));

					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + 
							"/pitmodgold - Shows the gold progression towards prestiging"));
					
					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + 
							"/pitmodbotting - Toggles on/off macro for vanished players"));
					
					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + 
							"/pitmodsearch [amount] - Searches for lobby with amount of players or less"));
					
					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + 
							"(/pitmodsearch to turn off)"));
					
					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + 
							"/pitmoddenick [name] - Attempts to denick a name"));

					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + 
							"/pitmod setapikey [key] - Sets the api key for gold tracker/nick detector"));

					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + 
							"(Note: Doing /api new sets the key automatically)"));

					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + 
							"/pitmod alert_sfx - Toggles on/off sounds for alerts"));

					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + 
							"/pitmod alert_center - Toggles on/off chat alert centering"));

					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + 
							"/pitmod alert_nicks - Toggles on/off chat alerting for nicked players"));
					
					sender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + 
							"/minecart - Checks for minecart in King's Map"));
					return;
				}
				String bool = args[0];
				JsonObject config = PitClient.getInstance().pitModConfigManager.getConfig();
				if (config.has(bool) && config.get(bool).isJsonPrimitive()
						&& config.get(bool).getAsJsonPrimitive().isBoolean()) {
					boolean val = !config.get(bool).getAsBoolean();
					config.addProperty(bool, Boolean.valueOf(val));
					sender.addChatMessage(new ChatComponentText(commandPrefix
							+ EnumChatFormatting.GREEN + "Set '" + bool + "' to " + ("" + val).toUpperCase()));
					PitClient.getInstance().pitModConfigManager.saveConfig();
				} else {
					sender.addChatMessage(new ChatComponentText(
							commandPrefix + EnumChatFormatting.RED + "Boolean '" + bool + "' not found"));
				}

			} else if (args.length == 2
					&& (args[0].equalsIgnoreCase("setapikey") || args[0].equalsIgnoreCase("apikey"))) {
				JsonObject config = PitClient.getInstance().pitModConfigManager.getConfig();
				config.addProperty("apikey", args[1]);
				PitClient.getInstance().pitModConfigManager.saveConfig();
				sender.addChatMessage(new ChatComponentText(
						commandPrefix + EnumChatFormatting.GREEN + "Api key set!"));
			} else {

				sender.addChatMessage(new ChatComponentText(
						commandPrefix + EnumChatFormatting.RED + "Too many args!"));
			}
		}
	});

	SimpleCommand pitModWarningCommand = new SimpleCommand("pitmodwarning", new SimpleCommand.ProcessCommandRunnable() {
		public void processCommand(ICommandSender sender, String[] args) {
			PitClient.getInstance().guiToOpen = new GuiWarningList(PitClient.getInstance().pitModConfigManager.getPlayerWarnings());
		}
	});
	
	SimpleCommand pitModDenick = new SimpleCommand("pitmoddenick", new SimpleCommand.ProcessCommandRunnable() {
		public void processCommand(ICommandSender sender, String[] args) {
			if (Minecraft.getMinecraft().theWorld.getPlayerEntityByName(args[0]) != null) {
				manager.denicker(args[0]);
			}
		}
	});
	
	SimpleCommand pitModBotting = new SimpleCommand("pitmodbotting", new SimpleCommand.ProcessCommandRunnable() {
		public void processCommand(ICommandSender sender, String[] args) {
			manager.vanishMacro = !manager.vanishMacro;
			String msg = manager.vanishMacro ? EnumChatFormatting.GREEN + "On" : EnumChatFormatting.RED + "Off";
			sender.addChatMessage(new ChatComponentText(
					commandPrefix + EnumChatFormatting.GREEN + "Botting macro turned " + msg));
		}
	});
	
	SimpleCommand pitModSearch = new SimpleCommand("pitmodsearch", new SimpleCommand.ProcessCommandRunnable() {
		public void processCommand(ICommandSender sender, String[] args) {
			if (args.length == 1) {
				int num = Integer.parseInt(args[0]);
				sender.addChatMessage(new ChatComponentText(commandPrefix + 
						EnumChatFormatting.GREEN + "Searching for lobby with " + 
						EnumChatFormatting.AQUA + num + 
						EnumChatFormatting.GREEN + " other players or less."));
				manager.search = true;
				manager.searchAmnt = num;
			} else {
				if (manager.search) {
					manager.search = false;
					sender.addChatMessage(new ChatComponentText(commandPrefix + EnumChatFormatting.RED + "Searching Stopped."));
				} else {
					sender.addChatMessage(new ChatComponentText(commandPrefix + EnumChatFormatting.RED + "Add number of players allowed. /pitmodsearch <num>"));

				}
			}
		}
	});
	
	SimpleCommand pitModMinecart = new SimpleCommand("minecart", new SimpleCommand.ProcessCommandRunnable() {
		public void processCommand(ICommandSender sender, String[] args) {
			manager.minecart();
		}
	});
	
	SimpleCommand pitModGui = new SimpleCommand("pitmodgui", new SimpleCommand.ProcessCommandRunnable() {
		public void processCommand(ICommandSender sender, String[] args) {
			PitClient.useNewGui = !PitClient.useNewGui;
			if (PitClient.useNewGui) sendMessageWithPrefix(EnumChatFormatting.AQUA + "You are now using the new gui");
			else sendMessageWithPrefix(EnumChatFormatting.DARK_RED + "You are now using the old gui");
		}
	});
	
	
}
