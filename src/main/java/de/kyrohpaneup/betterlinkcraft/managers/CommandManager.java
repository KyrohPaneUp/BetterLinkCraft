package de.kyrohpaneup.betterlinkcraft.managers;

import de.kyrohpaneup.betterlinkcraft.command.BLCCommand;
import de.kyrohpaneup.betterlinkcraft.command.impl.ConvertCommand;
import de.kyrohpaneup.betterlinkcraft.command.impl.GGCommand;
import net.minecraftforge.client.ClientCommandHandler;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager {

    public static ArrayList<BLCCommand> commands = new ArrayList<>();

    public void init() {
        commands.addAll(Arrays.asList(new GGCommand(), new ConvertCommand()));

        registerCommands();
    }

    private void registerCommands() {
        for (BLCCommand command : commands) {
            ClientCommandHandler.instance.registerCommand(command);
        }
    }
}
