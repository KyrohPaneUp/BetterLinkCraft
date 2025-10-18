package de.kyrohpaneup.betterlinkcraft.managers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.client.FMLClientHandler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ServerConnector {

    private static String pendingServerIP = "";
    private static String pendingServerName = "";
    private static boolean connectPending = false;
    private static int connectDelay = 2;

    public static void connectToServer(String serverIP, String serverName) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            pendingServerIP = serverIP;
            pendingServerName = serverName;
            connectPending = true;
            connectDelay = 2;
        });
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && connectPending) {
            if (connectDelay > 0) {
                connectDelay--;
            } else {
                executeConnect();
                connectPending = false;
            }
        }
    }

    private static void executeConnect() {
        try {
            ServerData serverData = new ServerData(
                    pendingServerName.isEmpty() ? "Server" : pendingServerName,
                    pendingServerIP,
                    false
            );

            System.out.println("Connecting to: " + pendingServerIP);
            FMLClientHandler.instance().connectToServer(null, serverData);

        } catch (Exception e) {
            System.err.println("Failed to connect: " + e.getMessage());
            e.printStackTrace();
        } finally {
            pendingServerIP = "";
            pendingServerName = "";
        }
    }
}
