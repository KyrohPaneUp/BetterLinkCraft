package de.kyrohpaneup.betterlinkcraft.command.impl;

import de.kyrohpaneup.betterlinkcraft.command.BLCCommand;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import de.kyrohpaneup.betterlinkcraft.managers.KeybindManager;
import de.kyrohpaneup.betterlinkcraft.managers.SheetManager;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class SheetCommand extends BLCCommand {

    public SheetCommand() {
        super("sheet");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        super.processCommand(sender, args);

        if (args.length != 1) {
            ChatManager.sendMessageWithPrefix("\u00A7cWrong usage: /sheet [sheetID]");
            return;
        }
        BLCSettings.updateOption(Option.SELECTED_SHEET, args[0]);
        ChatManager.sendMessageWithPrefix("\u00A7aYou set the sheet ID to " + args[0] + ".");
    }
}
