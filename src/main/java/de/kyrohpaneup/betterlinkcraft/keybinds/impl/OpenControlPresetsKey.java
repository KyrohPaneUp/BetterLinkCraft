package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.gui.impl.BLCControlPresetMenu;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import net.minecraft.client.Minecraft;

public class OpenControlPresetsKey extends BLCKeybind {

    public OpenControlPresetsKey() {
        super("Open Control Presets", 0, "BetterLinkCraft");
    }

    @Override
    public void onPressed() {
        Minecraft.getMinecraft().displayGuiScreen(new BLCControlPresetMenu(null));
    }

    @Override
    public void onReleased() {

    }

    @Override
    public void onKeyDown() {

    }
}
