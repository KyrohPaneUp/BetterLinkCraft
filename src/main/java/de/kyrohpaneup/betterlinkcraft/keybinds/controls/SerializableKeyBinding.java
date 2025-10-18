package de.kyrohpaneup.betterlinkcraft.keybinds.controls;

import net.minecraft.client.settings.KeyBinding;

public class SerializableKeyBinding {
    public String description;
    public int keyCode;

    public SerializableKeyBinding(String description, int keyCode) {
        this.description = description;
        this.keyCode = keyCode;
    }

    public static SerializableKeyBinding fromKeyBinding(KeyBinding kb) {
        return new SerializableKeyBinding(kb.getKeyDescription(), kb.getKeyCode());
    }

    public KeyBinding toKeyBinding(String category) {
        return new KeyBinding(description, keyCode, category);
    }
}

