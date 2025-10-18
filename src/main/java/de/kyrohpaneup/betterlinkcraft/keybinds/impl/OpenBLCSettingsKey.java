package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.gui.impl.BLCOptions;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import net.minecraft.client.Minecraft;

public class OpenBLCSettingsKey extends BLCKeybind {

    public OpenBLCSettingsKey() {
        super("Open BetterLC Settings", 0, "BetterLinkCraft");
    }

    @Override
    public void onPressed() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.displayGuiScreen(new BLCOptions(null));
        }
    }

    @Override
    public void onReleased() {

    }

    @Override
    public void onKeyDown() {

    }
}
