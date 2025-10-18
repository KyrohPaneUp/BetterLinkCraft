package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import de.kyrohpaneup.betterlinkcraft.managers.StratReminderManager;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import de.kyrohpaneup.betterlinkcraft.settings.optionEnums.ShowReminders;
import org.lwjgl.input.Keyboard;

public class ShowStratRemindersKey extends BLCKeybind {

    StratReminderManager srm;

    public ShowStratRemindersKey() {
        super("Show Strat Reminders", Keyboard.KEY_GRAVE, "BetterLinkCraft");
        srm = BetterLinkCraft.INSTANCE.getSrm();
    }

    @Override
    public void onPressed() {
        if (Option.SHOW_REMINDERS.getEnumValue() == ShowReminders.TOGGLE) {
            StratReminderManager.showReminders = !StratReminderManager.showReminders;
        } else {
            StratReminderManager.showReminders = true;
        }
    }

    @Override
    public void onReleased() {
        if (Option.SHOW_REMINDERS.getEnumValue() != ShowReminders.TOGGLE) {
            StratReminderManager.showReminders = false;
        }
    }

    @Override
    public void onKeyDown() {

    }
}
