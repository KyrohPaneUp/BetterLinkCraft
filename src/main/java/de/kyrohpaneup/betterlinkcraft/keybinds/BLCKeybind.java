package de.kyrohpaneup.betterlinkcraft.keybinds;

import net.minecraft.client.settings.KeyBinding;

public abstract class BLCKeybind {
    private final KeyBinding keyBinding;

    public BLCKeybind(String description, int keyCode, String category) {
        this.keyBinding = new KeyBinding(description, keyCode, category);
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public abstract void onPressed();

    public abstract void onReleased();

    public abstract void onKeyDown();
}
