package de.kyrohpaneup.betterlinkcraft.command.impl;

import de.kyrohpaneup.betterlinkcraft.command.BLCCommand;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class BLCTeleportCommand extends BLCCommand {

    public BLCTeleportCommand() {
        super("blcteleport");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        super.processCommand(sender, args);

        if (args.length == 0) {
            sendUsage();
            return;
        }

        String name = args[0];
        if (name == null) {
            sendUsage();
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();

        if (mc.theWorld == null) {
            ChatManager.sendMessageWithPrefix("\u00A7cSomething went wrong!");
            return;
        }

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player.getName().equalsIgnoreCase(name)) {
                teleport(player);
                return;
            }
        }
        ChatManager.sendMessageWithPrefix("\u00A7cNo player with the name " + name + " was found.");
    }

    private void sendUsage() {
        ChatManager.sendMessageWithPrefix("\u00A7cUsage: /blctp <player>");
    }

    private void teleport(EntityPlayer player) {
        if (player == null) return;
        double x  = player.posX;
        double y = player.posY;
        double z = player.posZ;

        Minecraft.getMinecraft().thePlayer.sendChatMessage("/tp " + x + " " + y + " " + z);
    }
}
