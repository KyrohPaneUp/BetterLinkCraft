package de.kyrohpaneup.betterlinkcraft.keybinds.controls;

import de.kyrohpaneup.betterlinkcraft.managers.KeybindManager;
import net.minecraft.client.settings.KeyBinding;

import java.util.HashMap;
import java.util.Map;

public class ControlPreset {
    private String name;
    private boolean isPrimary = false;
    private boolean isSecondary = false;
    private final Map<String, Integer> bindings;
    private float sensitivity;

    public ControlPreset(String name) {
        this.name = name;
        this.bindings = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isSecondary() {
        return isSecondary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public void setSecondary(boolean secondary) {
        isSecondary = secondary;
    }

    public Map<String, Integer> getBindings() {
        return bindings;
    }

    public void addBinding(String description, int keyCode) {
        bindings.put(description, keyCode);
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void apply(KeyBinding[] allBindings) {
        for (KeyBinding kb : allBindings) {
            if (bindings.containsKey(kb.getKeyDescription())) {
                kb.setKeyCode(bindings.get(kb.getKeyDescription()));
            }
        }
        KeybindManager.selectedPreset = this;
        KeyBinding.resetKeyBindingArrayAndHash();
    }
}
