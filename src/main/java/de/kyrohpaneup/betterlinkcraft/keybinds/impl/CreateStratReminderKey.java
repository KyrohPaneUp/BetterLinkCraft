package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.gui.impl.strat.CreateStratReminderGui;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import de.kyrohpaneup.betterlinkcraft.managers.StratReminderManager;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;

public class CreateStratReminderKey extends BLCKeybind {

    StratReminderManager srm;

    public CreateStratReminderKey() {
        super("Create Strat Reminder", Keyboard.KEY_K, "BetterLinkCraft");
        srm = BetterLinkCraft.INSTANCE.getSrm();
    }

    @Override
    public void onPressed() {
        HashMap<String, String> map = new HashMap<>();
        map.put("map id", Option.SELECTED_SR_MAP.getStringValue());
        Minecraft.getMinecraft()
                .displayGuiScreen(
                        new CreateStratReminderGui(map, false));
    }

    @Override
    public void onReleased() {

    }

    @Override
    public void onKeyDown() {

    }
}
