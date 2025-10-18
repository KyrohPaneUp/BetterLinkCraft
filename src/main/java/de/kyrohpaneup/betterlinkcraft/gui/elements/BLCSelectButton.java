package de.kyrohpaneup.betterlinkcraft.gui.elements;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.keybinds.controls.ControlPreset;
import de.kyrohpaneup.betterlinkcraft.managers.KeybindManager;
import net.minecraft.client.gui.GuiButton;

public class BLCSelectButton extends GuiButton {

    boolean selected;
    ControlPreset preset;
    KeybindManager keybindManager = BetterLinkCraft.INSTANCE.getKeybindManager();

    public BLCSelectButton(int buttonId, ControlPreset preset, int x, int y, int width, int height, String buttonText) {
        super(buttonId, x, y, width, height, buttonText);
        this.preset = preset;
        selected = preset.getName().equalsIgnoreCase(KeybindManager.selectedPreset.getName());
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void select() {
        this.selected = true;
        keybindManager.applyPreset(preset);
    }

    public ControlPreset getPreset() {
        return preset;
    }

    public void setPreset(ControlPreset preset) {
        this.preset = preset;
    }
}
