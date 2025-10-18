package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import de.kyrohpaneup.betterlinkcraft.managers.PracticeManager;
import org.lwjgl.input.Keyboard;

public class SwitchToUgisKey extends BLCKeybind {

    PracticeManager practiceManager = BetterLinkCraft.INSTANCE.getPracticeManager();

    public SwitchToUgisKey() {
        super("Quick Ugis Practice", Keyboard.KEY_P, "BetterLinkCraft");
    }

    @Override
    public void onPressed() {
        practiceManager.switchServer();
    }

    @Override
    public void onReleased() {

    }

    @Override
    public void onKeyDown() {

    }
}
