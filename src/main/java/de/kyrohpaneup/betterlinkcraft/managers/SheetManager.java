package de.kyrohpaneup.betterlinkcraft.managers;

import de.kyrohpaneup.betterlinkcraft.gui.impl.sheet.GuiSheetViewer;
import de.kyrohpaneup.betterlinkcraft.mods.sheet.GoogleSheetsAPI;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.function.Consumer;

public class SheetManager {

    public static GoogleSheetsAPI.SheetInfo SELECTED = null;
    public static GoogleSheetsAPI.SheetData DATA = null;
    public static GuiSheetViewer SHEET = null;
    public static List<GoogleSheetsAPI.SheetInfo> INFO_LIST = null;

    public static void init() {
        Option option = Option.SELECTED_SHEET;
        String id = option.getStringValue();
        if (id == null || id.replace(" ", "").equals("")) {
            return;
        }
        loadSheetDataAsync(id, (sheetData) -> {});
    }

    public static void loadSheetDataAsync(String id, Consumer<GoogleSheetsAPI.SheetData> callback) {
        new Thread(() -> {
            GoogleSheetsAPI.SheetData resultData = null;

            try {
                INFO_LIST = GoogleSheetsAPI.getAllSheets();

                if (id != null && !id.trim().isEmpty()) {
                    resultData = GoogleSheetsAPI.getSheetData(id);
                    DATA = resultData;
                }

            } catch (Exception e) {
                System.err.println("Error loading sheet data: " + e.getMessage());
                resultData = null;
            }

            final GoogleSheetsAPI.SheetData finalData = resultData;
            Minecraft.getMinecraft().addScheduledTask(() -> callback.accept(finalData));

        }, "GoogleSheets-Loader").start();
    }
}
