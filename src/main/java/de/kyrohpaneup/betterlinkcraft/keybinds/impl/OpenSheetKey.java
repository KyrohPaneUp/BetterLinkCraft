package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.gui.impl.sheet.GuiSheetViewer;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import de.kyrohpaneup.betterlinkcraft.managers.SheetManager;
import de.kyrohpaneup.betterlinkcraft.mods.sheet.GoogleSheetsAPI;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class OpenSheetKey extends BLCKeybind {

    public OpenSheetKey() {
        super("Open Sheet", Keyboard.KEY_J, "BetterLinkCraft");
    }

    @Override
    public void onPressed() {
        if (SheetManager.DATA == null || !String.valueOf(SheetManager.DATA.sheet.id).equals(Option.SELECTED_SHEET.getStringValue())) {
            SheetManager.DATA = GoogleSheetsAPI.getSheetData(Option.SELECTED_SHEET.getStringValue());
        }

        if (SheetManager.SHEET == null) {
            SheetManager.SHEET = new GuiSheetViewer(SheetManager.DATA);
        } else if (SheetManager.SHEET.sheetData != SheetManager.DATA) {
            SheetManager.SHEET = new GuiSheetViewer(SheetManager.DATA);
        }

        Minecraft.getMinecraft().displayGuiScreen(SheetManager.SHEET);
    }

    @Override
    public void onReleased() {

    }

    @Override
    public void onKeyDown() {

    }
}
