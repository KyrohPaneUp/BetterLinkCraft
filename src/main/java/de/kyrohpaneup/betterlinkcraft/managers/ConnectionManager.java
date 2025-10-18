package de.kyrohpaneup.betterlinkcraft.managers;


import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ConnectionManager {

    private enum ConnectionState {
        IDLE, CONNECTING, CONNECTED, LOADING_WORLD, COMPLETE
    }

    private ConnectionState state = ConnectionState.IDLE;
    private Runnable onCompleteAction;
    private int waitTicks = 40;
    private int timeoutTicks = 400; // 20s Timeout

    public void joinServerWithCallback(String serverIP, Runnable onComplete) {
        this.waitTicks = (int) (Option.UGIS_PRAC_DELAY.getDoubleValue() * 20);
        this.state = ConnectionState.CONNECTING;
        this.onCompleteAction = onComplete;
        this.timeoutTicks = 400;

        ServerConnector.connectToServer(serverIP, "AutoJoin");
    }

    @SubscribeEvent
    public void onClientConnected(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (state == ConnectionState.CONNECTING) {
            state = ConnectionState.CONNECTED;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && state != ConnectionState.IDLE) {

            if (timeoutTicks > 0) {
                timeoutTicks--;
            } else {
                System.out.println("Connection timeout");
                state = ConnectionState.IDLE;
                return;
            }

            switch (state) {
                case CONNECTED:
                    if (waitTicks > 0) {
                        waitTicks--;
                    } else {
                        state = ConnectionState.LOADING_WORLD;
                    }
                    break;

                case LOADING_WORLD:
                    Minecraft mc = Minecraft.getMinecraft();
                    if (mc.theWorld != null && mc.thePlayer != null) {
                        state = ConnectionState.COMPLETE;
                        executeCompletionAction();
                    }
                    break;

                case COMPLETE:
                    state = ConnectionState.IDLE;
                    break;
            }
        }
    }

    private void executeCompletionAction() {
        if (onCompleteAction != null) {
            try {
                onCompleteAction.run();
            } catch (Exception e) {
                System.err.println("Error executing completion action: " + e.getMessage());
            }
        }

        onCompleteAction = null;
    }
}
