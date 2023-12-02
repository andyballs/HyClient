package ru.mdashlw.hypixel.pit.stats.command;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import rage.pitclient.PitClient;

public abstract class Command implements ICommand {

    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    public List getCommandAliases() {
        return Collections.emptyList();
    }

    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return null;
    }

    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    public int compareTo(ICommand o) {
        return this.getCommandName().compareTo(o.getCommandName());
    }

    public void register() {
        PitClient.commandManager.instance.registerCommand(this);
    }
}
