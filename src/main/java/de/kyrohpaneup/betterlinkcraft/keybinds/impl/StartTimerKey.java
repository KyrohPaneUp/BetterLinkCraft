package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import de.kyrohpaneup.betterlinkcraft.managers.TimerManager;
import de.kyrohpaneup.betterlinkcraft.settings.Option;

public class StartTimerKey extends BLCKeybind {

    TimerManager timerManager;

    public StartTimerKey() {
        super("Start/Restart Timer", 0, "BetterLinkCraft");
        timerManager = BetterLinkCraft.INSTANCE.getTimerManager();
    }

    @Override
    public void onPressed() {
        if (!Option.TIMER_ENABLED.getBooleanValue()) {
            return;
        }
        timerManager.startTimer();
    }

    @Override
    public void onReleased() {

    }

    @Override
    public void onKeyDown() {

    }
}
