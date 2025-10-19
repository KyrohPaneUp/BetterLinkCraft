package de.kyrohpaneup.betterlinkcraft.managers;

import de.kyrohpaneup.betterlinkcraft.mods.autotext.CustomCommand;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCommandManager {

    private final List<CustomCommand> customCommands = new ArrayList<>();
    private final Map<String, CommandBase> registeredCommands = new HashMap<>();

    public List<CustomCommand> getCustomCommands() {
        return customCommands;
    }

    public void removeCustomCommand(String name) {
        CustomCommand command = getCustomCommand(name);
        if (command != null) {
            customCommands.remove(command);
            String commandName = command.getCommand().startsWith("/")
                    ? command.getCommand().substring(1)
                    : command.getCommand();
            unregisterCommand(commandName);
        }
    }

    public CustomCommand getCustomCommand(String name) {
        for (CustomCommand command : customCommands) {
            if (command.getName().equalsIgnoreCase(name)) return command;
        }
        return null;
    }

    public void setCustomCommands(List<CustomCommand> newCommands) {
        unregisterAllCommands();

        this.customCommands.clear();

        for (CustomCommand command : newCommands) {
            this.customCommands.add(command);
            registerCommand(command);
        }
    }

    private void unregisterAllCommands() {
        try {
            Map<String, ?> commandMap = ClientCommandHandler.instance.getCommands();
            for (String commandName : new ArrayList<>(registeredCommands.keySet())) {
                commandMap.remove(commandName);
            }
            registeredCommands.clear();

        } catch (Exception e) {
            System.err.println("Failed to unregister all commands: " + e.getMessage());
        }
    }

    private void registerCommand(CustomCommand command) {
        String commandName = command.getCommand().startsWith("/")
                ? command.getCommand().substring(1)
                : command.getCommand();

        CustomCommandWrapper wrapper = new CustomCommandWrapper(commandName, command);
        ClientCommandHandler.instance.registerCommand(wrapper);
        registeredCommands.put(commandName, wrapper);
    }

    private void unregisterCommand(String commandName) {
        try {
            Map<String, ?> commandMap = ClientCommandHandler.instance.getCommands();
            commandMap.remove(commandName);
            registeredCommands.remove(commandName);
        } catch (Exception e) {
            System.err.println("Failed to unregister command '" + commandName + "': " + e.getMessage());
        }
    }

    private static class CustomCommandWrapper extends CommandBase {
        private final String commandName;
        private final CustomCommand customCommand;

        public CustomCommandWrapper(String commandName, CustomCommand customCommand) {
            this.commandName = commandName;
            this.customCommand = customCommand;
        }

        @Override
        public String getCommandName() {
            return commandName;
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/" + commandName;
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            customCommand.execute();
        }
    }
}