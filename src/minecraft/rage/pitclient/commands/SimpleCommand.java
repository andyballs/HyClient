package rage.pitclient.commands;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public class SimpleCommand extends CommandBase {
	private String commandName;
	private ProcessCommandRunnable runnable;
	private TabCompleteRunnable tabRunnable;

	public SimpleCommand(String commandName, ProcessCommandRunnable runnable) {
		this.commandName = commandName;
		this.runnable = runnable;
	}

	public SimpleCommand(String commandName, ProcessCommandRunnable runnable, TabCompleteRunnable tabRunnable) {
		this.commandName = commandName;
		this.runnable = runnable;
		this.tabRunnable = tabRunnable;
	}

	public static abstract class ProcessCommandRunnable {
		public abstract void processCommand(ICommandSender param1ICommandSender, String[] param1ArrayOfString);
	}

	public static abstract class TabCompleteRunnable {
		public abstract List<String> tabComplete(ICommandSender param1ICommandSender, String[] param1ArrayOfString,
				BlockPos param1BlockPos);
	}

	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	public String getCommandName() {
		return this.commandName;
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.commandName;
	}

	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		this.runnable.processCommand(sender, args);
	}

	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		if (this.tabRunnable != null)
			return this.tabRunnable.tabComplete(sender, args, pos);
		return null;
	}
}
