package de.kyrohpaneup.betterlinkcraft.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;

public class BLCCommand extends CommandBase {

    private final String cmd;
    private String usage;
    public ArrayList<BLCCommand> subCommands = new ArrayList<>();
    public BLCCommand parentCommand;

    public BLCCommand(String cmd) {
        this.cmd = cmd;
    }

    @Override
    public String getCommandName() {
        return cmd;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return usage;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {

    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getFullCommandString(String[] args) {
        if (args.length == 0) return "";
        return "convert " + String.join(" ", args);
    }
}
