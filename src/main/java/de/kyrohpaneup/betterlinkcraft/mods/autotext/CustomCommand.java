package de.kyrohpaneup.betterlinkcraft.mods.autotext;

import net.minecraft.client.Minecraft;

public class CustomCommand {

    private String name;
    private String command;
    private String output;

    public CustomCommand(String name, String command, String output) {
        this.name = name;
        this.command = command;
        this.output = output;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }

    public void execute() {
        if (output != null && !output.isEmpty()) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage(output);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CustomCommand command = (CustomCommand) obj;
        return name.equals(command.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return String.format("CustomCommand{name='%s', command=%s, output='%s'}", name, command, output);
    }
}
