package de.kyrohpaneup.betterlinkcraft.managers;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class PracticeManager {

    ConnectionManager connectionManager = BetterLinkCraft.INSTANCE.getConnectionManager();
    String serverIp = "linkcraft.mcpro.io";
    LinkCraftManager linkCraftManager = BetterLinkCraft.INSTANCE.getLinkCraftManager();

    public void copyCoordinates() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        float yaw = player.rotationYaw;
        float pitch = player.rotationPitch;
        String posString = x + " " + y + " " + z + " " + yaw + " " + pitch;

        GuiScreen.setClipboardString(posString);
        ChatManager.sendMessageWithPrefix("§aYour coordinates have been copied to your clipboard.");
    }

    public void switchServer() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (mc.getCurrentServerData() == null) return;

        String currentServerIp = mc.getCurrentServerData().serverIP;

        if (LinkCraftManager.isOnLinkCraft()) {
            serverIp = currentServerIp.toLowerCase();

            double x = player.posX;
            double y = player.posY;
            double z = player.posZ;
            float yaw = player.rotationYaw;
            float pitch = player.rotationPitch;
            String posString = x + " " + y + " " + z + " " + yaw + " " + pitch;

            linkCraftManager.sendPluginMessage(() -> {

                if (LinkCraftManager.NO_PRACTICE) {
                    StringSelection stringSelection = new StringSelection(posString);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    ChatManager.sendMessageWithPrefix("§aYour coordinates have been copied to your clipboard.");
                } else {
                    if (mc.theWorld != null) {
                        mc.theWorld.sendQuittingDisconnectingPacket();
                        connectionManager.joinServerWithCallback("ugis.pro", () -> mc.thePlayer.sendChatMessage("/tp " + posString));
                    }
                }
            });

        } else if (currentServerIp.equalsIgnoreCase("ugis.pro")) {
            if (LinkCraftManager.NO_PRACTICE) {
                ChatManager.sendMessageWithPrefix("§cYou can't do this in a no practice map.");
                return;
            }
            mc.addScheduledTask(() -> {
                if (mc.theWorld != null) {
                    mc.theWorld.sendQuittingDisconnectingPacket();
                    connectionManager.joinServerWithCallback(serverIp, () -> {});
                }
            });
        }
    }
}
