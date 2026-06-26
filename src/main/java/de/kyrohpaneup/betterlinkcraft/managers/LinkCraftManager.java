package de.kyrohpaneup.betterlinkcraft.managers;

import com.google.gson.Gson;
import de.kyrohpaneup.betterlinkcraft.utils.Request;
import de.kyrohpaneup.betterlinkcraft.utils.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.IOException;

public class LinkCraftManager {

    public static final String CHANNEL = "LinkCraftAPI";
    public static boolean NO_PRACTICE = true;
    private Runnable pendingResponseCallback = null;

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

    @SubscribeEvent
    public void onServerPacketReceive(FMLNetworkEvent.ClientCustomPacketEvent event) {
        if (!event.packet.channel().equals(CHANNEL)) return;

        ByteBuf payload = event.packet.payload();
        try (ByteBufInputStream in = new ByteBufInputStream(payload)) {
            String jsonString = in.readUTF();

            Gson gson = new Gson();
            Response response = gson.fromJson(jsonString, Response.class);

            Minecraft.getMinecraft().addScheduledTask(() -> handleServerData(response));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleServerData(Response data) {
        if (data == null) return;

        NO_PRACTICE = data.isNoPrac;

        if (pendingResponseCallback != null) {
            pendingResponseCallback.run();
            pendingResponseCallback = null;
        }
    }

    public void sendPluginMessage() {
        sendPluginMessage(null);
    }

    public void sendPluginMessage(Runnable callback) {
        if (Minecraft.getMinecraft().getNetHandler() == null) return;

        this.pendingResponseCallback = callback;

        Request request = new Request("isNoPrac");
        Gson gson = new Gson();
        String jsonString = gson.toJson(request);

        ByteBuf buf = Unpooled.buffer();
        try (ByteBufOutputStream out = new ByteBufOutputStream(buf)) {
            out.writeUTF(jsonString);

            PacketBuffer packetBuffer = new PacketBuffer(buf);
            C17PacketCustomPayload packet = new C17PacketCustomPayload(CHANNEL, packetBuffer);
            Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
