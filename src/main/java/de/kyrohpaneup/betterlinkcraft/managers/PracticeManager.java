package de.kyrohpaneup.betterlinkcraft.managers;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class PracticeManager {

    ConnectionManager connectionManager = BetterLinkCraft.INSTANCE.getConnectionManager();
    String serverIp = "linkcraft.mcpro.io";

    public void switchServer() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (LinkCraftManager.isOnLinkCraft() || LinkCraftManager.isOnJumpCraft()) {
            if (mc.getCurrentServerData() != null) {
                serverIp = mc.getCurrentServerData().serverIP.toLowerCase();
            }
            double x = player.posX;
            double y = player.posY;
            double z = player.posZ;
            float yaw = player.rotationYaw;
            float pitch = player.rotationPitch;

            String posString = x + " " + y + " " + z + " " + yaw + " " + pitch;

            mc.addScheduledTask(() -> {
                if (mc.theWorld != null) {
                    mc.theWorld.sendQuittingDisconnectingPacket();

                    connectionManager.joinServerWithCallback("ugis.pro", () -> mc.thePlayer.sendChatMessage("/tp " + posString));
                }
            });
        } else if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null && mc.getCurrentServerData().serverIP.equalsIgnoreCase("ugis.pro")) {
            mc.addScheduledTask(() -> {
                if (mc.theWorld != null) {
                    mc.theWorld.sendQuittingDisconnectingPacket();

                    connectionManager.joinServerWithCallback(serverIp, () -> {});
                }
            });
        }
    }
}
