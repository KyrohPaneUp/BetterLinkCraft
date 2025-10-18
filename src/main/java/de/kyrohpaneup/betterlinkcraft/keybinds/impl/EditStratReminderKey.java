package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.gui.impl.strat.CreateStratReminderGui;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import de.kyrohpaneup.betterlinkcraft.managers.StratReminderManager;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditStratReminderKey extends BLCKeybind {

    StratReminderManager srm;

    public EditStratReminderKey() {
        super("Edit Strat Reminder", Keyboard.KEY_L, "BetterLinkCraft");
        srm = BetterLinkCraft.INSTANCE.getSrm();
    }

    @Override
    public void onPressed() {
        List<Map<String, String>> maps = srm.getAllRemindersMap();
        if (maps.isEmpty()) {
            ChatManager.sendMessageWithPrefix("\u00A7cYou don't have any Reminders for " + Option.SELECTED_SR_MAP.getStringValue() + " yet!");
            return;
        }
        Minecraft.getMinecraft()
                .displayGuiScreen(
                        new CreateStratReminderGui(maps.get(0), true));
    }

    @Override
    public void onReleased() {

    }

    @Override
    public void onKeyDown() {

    }
}
