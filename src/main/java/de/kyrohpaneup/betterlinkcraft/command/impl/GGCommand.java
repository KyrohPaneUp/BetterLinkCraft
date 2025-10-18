package de.kyrohpaneup.betterlinkcraft.command.impl;

import de.kyrohpaneup.betterlinkcraft.command.BLCCommand;
import de.kyrohpaneup.betterlinkcraft.managers.LinkCraftManager;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class GGCommand extends BLCCommand {

    public GGCommand() {
        super("gg");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        super.processCommand(sender, args);

        if (!LinkCraftManager.isOnLinkCraft() || args.length != 0) {
            StringBuilder builder = new StringBuilder("gg");
            for (String arg : args) {
                builder.append(" ").append(arg);
            }
            Minecraft.getMinecraft().thePlayer.sendChatMessage(builder.toString());
            return;
        }

        if (Option.GG_COLOR.getEnumValue() == null) return;
        Minecraft.getMinecraft().thePlayer.sendChatMessage(Option.GG_COLOR.getEnumValue().getValue());
    }
}
