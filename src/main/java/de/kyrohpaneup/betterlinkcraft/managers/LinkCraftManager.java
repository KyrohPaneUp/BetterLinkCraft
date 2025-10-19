package de.kyrohpaneup.betterlinkcraft.managers;

import net.minecraft.client.Minecraft;

public class LinkCraftManager {

    public static boolean isOnLinkCraft() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc == null || mc.getCurrentServerData() == null) {
            return false;
        }

        String serverIP = mc.getCurrentServerData().serverIP;
        return serverIP != null && serverIP.equalsIgnoreCase("linkcraft.mcpro.io");
    }

    public static boolean isOnJumpCraft() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc == null || mc.getCurrentServerData() == null) {
            return false;
        }

        String serverIP = mc.getCurrentServerData().serverIP;
        return serverIP != null && serverIP.equalsIgnoreCase("play.jumpcraft2.org");
    }
}
