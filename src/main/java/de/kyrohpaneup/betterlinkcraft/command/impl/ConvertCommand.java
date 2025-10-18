package de.kyrohpaneup.betterlinkcraft.command.impl;

import de.kyrohpaneup.betterlinkcraft.command.BLCCommand;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
public class ConvertCommand extends BLCCommand {

    public ConvertCommand() {
        super("convert");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        super.processCommand(sender, args);

        if (args.length == 0) {
            ChatManager.sendMessageWithPrefix("\u00A7cUsage: /convert [.xxx], [.yyy], [.zzz]..");
            return;
        }

        ChatManager.sendMessage("\u00A73\u00A7nConverted Coordinates:");
        ChatManager.sendMessage("");
        for (String arg : args) {
            double d = parseDecimal(arg);

            String decimals = arg.substring(arg.indexOf('.') + 1);
            String formatted = String.format("%." + decimals.length() + "f", 1.0 - d);


            formatted = formatted.replaceFirst("^0", "");
            formatted = formatted.replaceAll(",", ".");

            ChatManager.sendMessage("\u00A7a" + arg + " \u00A72-> \u00A7b" + formatted);
        }
    }

    private double parseDecimal(String input) {
        if (input == null || !input.matches("\\.\\d{1,16}")) {
            System.out.println("Wrong input: " + input);
            return 0.0;
        }

        return Double.parseDouble("0" + input);
    }
}

