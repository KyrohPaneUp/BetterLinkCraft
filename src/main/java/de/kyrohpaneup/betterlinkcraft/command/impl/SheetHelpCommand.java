package de.kyrohpaneup.betterlinkcraft.command.impl;

import de.kyrohpaneup.betterlinkcraft.command.BLCCommand;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class SheetHelpCommand extends BLCCommand {

    public SheetHelpCommand() {
        super("sheethelp");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        super.processCommand(sender, args);

        ChatManager.sendMessage("\u00A78To manually set a StratSheet do the following:");
        ChatManager.sendMessage("\u00A771st: Open the editor-stratsheet");
        ChatManager.sendMessage("\u00A772nd: Select the Sheet you want to see ingame");
        ChatManager.sendMessage("\u00A773rd: Click on the link in the search bar");
        ChatManager.sendMessage("\u00A774th: Copy the numbers at the end of the link after 'gid='");
        ChatManager.sendMessage("\u00A775th: Use the command /sheet [numbers that you copied]");
    }
}
