package de.kyrohpaneup.betterlinkcraft.managers;

import de.kyrohpaneup.betterlinkcraft.command.BLCCommand;
import de.kyrohpaneup.betterlinkcraft.command.impl.ConvertCommand;
import de.kyrohpaneup.betterlinkcraft.command.impl.GGCommand;
import de.kyrohpaneup.betterlinkcraft.command.impl.SheetCommand;
import de.kyrohpaneup.betterlinkcraft.command.impl.SheetHelpCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class CommandManager {

    public static ArrayList<BLCCommand> commands = new ArrayList<>();
    private GGCommand ggCommand;

    public void init() {
        this.ggCommand = new GGCommand();
        commands.addAll(Arrays.asList(new ConvertCommand(), new SheetHelpCommand(), new SheetCommand()));

        registerCommands();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerCommands() {
        for (BLCCommand command : commands) {
            ClientCommandHandler.instance.registerCommand(command);
        }
    }

    @SubscribeEvent
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        String serverAddress = getCurrentServerAddress();
        if (serverAddress.contains("linkcraft.mcpro.io")) {
            registerCommand();
        }
    }

    @SubscribeEvent
    public void onServerLeave(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        unregisterCommand();
    }

    private String getCurrentServerAddress() {
        ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
        return serverData != null ? serverData.serverIP : "";
    }

    public void registerCommand() {
        ClientCommandHandler.instance.registerCommand(ggCommand);
    }

    private void unregisterCommand() {
        try {
            Map<String, ?> commandMap = ClientCommandHandler.instance.getCommands();
            commandMap.remove(ggCommand.getCommandName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
