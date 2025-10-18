package de.kyrohpaneup.betterlinkcraft.mods.autotext;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class AutoText {
    private String name;
    private int key;
    private String output;
    private String temporaryType;

    public AutoText(String name, int key, String output) {
        this.name = name;
        this.key = key;
        this.output = output;
    }

    public AutoText(String name, String keyName, String output) {
        this.name = name;
        this.key = Keyboard.getKeyIndex(keyName.toUpperCase());
        this.output = output;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }
    public void setKey(String keyName) { this.key = Keyboard.getKeyIndex(keyName.toUpperCase()); }

    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }

    public String getTemporaryType() { return temporaryType; }
    public void setTemporaryType(String type) { this.temporaryType = type; }

    public String getKeyName() {
        return Keyboard.getKeyName(key);
    }

    public void execute() {
        if (output != null && !output.isEmpty()) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage(output);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AutoText autoText = (AutoText) obj;
        return name.equals(autoText.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return String.format("AutoText{name='%s', key=%s, output='%s'}", name, getKeyName(), output);
    }
}