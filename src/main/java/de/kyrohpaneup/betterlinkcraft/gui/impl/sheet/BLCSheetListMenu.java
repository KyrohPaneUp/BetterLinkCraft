package de.kyrohpaneup.betterlinkcraft.gui.impl.sheet;

import de.kyrohpaneup.betterlinkcraft.managers.SheetManager;
import de.kyrohpaneup.betterlinkcraft.mods.sheet.GoogleSheetsAPI;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;

import java.io.IOException;
import java.util.List;

public class BLCSheetListMenu extends GuiScreen {

    private final GuiScreen parentScreen;
    private BLCSheetListMenu.SheetList sheetList;

    public BLCSheetListMenu(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.sheetList = new BLCSheetListMenu.SheetList(this.mc);

        int bottomY = this.height - 28;

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(101, this.width / 2 - 100, bottomY - 25, 200, 20, "Load Sheet"));
        this.buttonList.add(new GuiButton(100, this.width / 2 - 100, bottomY, 200, 20, "Done"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        this.sheetList.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(this.fontRendererObj, "StratSheets", this.width / 2, 15, 0xFFFFFF);

        if (sheetList.sheets == null || sheetList.sheets.isEmpty()) {
            drawCenteredString(fontRendererObj, "No data available, try reloading this GUI", width / 2, height / 2, 0xFF6666);
            drawCenteredString(fontRendererObj, "If this keeps not working, type /sheethelp in the chat.", width / 2, height / 2 + 20, 0xFF6666);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 100) { // Done
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (button.id == 101) {
            if (SheetManager.SELECTED == null) {
                return;
            }
            BLCSettings.updateOption(Option.SELECTED_SHEET, String.valueOf(SheetManager.SELECTED.id));
            SheetManager.DATA = GoogleSheetsAPI.getSheetData(String.valueOf(SheetManager.SELECTED.id));
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.sheetList.handleMouseInput();
    }

    private class SheetList extends GuiSlot {

        private final Minecraft mc;
        private final List<GoogleSheetsAPI.SheetInfo> sheets;

        public SheetList(Minecraft mc) {
            super(mc, BLCSheetListMenu.this.width, BLCSheetListMenu.this.height,
                    40, BLCSheetListMenu.this.height - 60, 22);
            this.mc = mc;
            this.sheets = SheetManager.INFO_LIST == null || SheetManager.INFO_LIST.isEmpty() ? GoogleSheetsAPI.getAllSheets() : SheetManager.INFO_LIST;
            SheetManager.INFO_LIST = sheets;
        }

        @Override
        protected int getSize() {
            return sheets.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            if (sheets.isEmpty()) return;
            SheetManager.SELECTED = sheets.get(index);
        }

        @Override
        protected boolean isSelected(int index) {
            return SheetManager.SELECTED != null && SheetManager.SELECTED.name.equalsIgnoreCase(sheets.get(index).name);
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int index, int x, int y, int height, int mouseX, int mouseY) {
            GoogleSheetsAPI.SheetInfo info = sheets.get(index);
            String name = info.name;
            int color = 0xFFFFFF;

            mc.fontRendererObj.drawString(name, x + 5, y + 6, color);
        }
    }
}
