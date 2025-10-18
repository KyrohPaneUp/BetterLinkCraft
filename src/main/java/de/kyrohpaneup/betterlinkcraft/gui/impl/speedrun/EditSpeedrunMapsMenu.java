package de.kyrohpaneup.betterlinkcraft.gui.impl.speedrun;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.gui.StringGui;
import de.kyrohpaneup.betterlinkcraft.gui.impl.options.BLCSetStringMenu;
import de.kyrohpaneup.betterlinkcraft.managers.ConfigManager;
import de.kyrohpaneup.betterlinkcraft.managers.TimerManager;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;

import java.io.IOException;
import java.util.List;

public class EditSpeedrunMapsMenu extends StringGui {

    private final GuiScreen parentScreen;
    private MapList mapList;
    private final TimerManager timerManager;
    private final ConfigManager configManager;

    public EditSpeedrunMapsMenu(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.timerManager = BetterLinkCraft.INSTANCE.getTimerManager();
        this.configManager = BetterLinkCraft.INSTANCE.getConfigManager();
    }

    @Override
    public void initGui() {
        super.initGui();

        this.mapList = new MapList(this.mc);

        int buttonWidth = 200 / 3;
        int bottomY = this.height - 28;

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(101, this.width / 2 - 100, bottomY - 25, buttonWidth, 20, "Create"));
        this.buttonList.add(new GuiButton(102, this.width / 2 - 100 + buttonWidth + 1, bottomY - 25, buttonWidth, 20, "Rename"));
        this.buttonList.add(new GuiButton(103, this.width / 2 - 100 + (buttonWidth * 2) + 2, bottomY - 25, buttonWidth, 20, "Delete"));
        this.buttonList.add(new GuiButton(100, this.width / 2 - 100, bottomY, 200, 20, "Done"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        this.mapList.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(this.fontRendererObj, "Speedrun Maps", this.width / 2, 15, 0xFFFFFF);
        //drawCenteredString(this.fontRendererObj, "Click and hold to drag maps", this.width / 2, 30, 0xAAAAAA);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        configManager.saveSpeedrunSegments();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 100) { // Done
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (button.id == 101) { // Create
            this.mc.displayGuiScreen(new BLCSetStringMenu("", this));
        }
        if (button.id == 102) { // Rename
            String selected = mapList.getSelected();
            if (selected != null && !selected.isEmpty()) {
                this.mc.displayGuiScreen(new BLCSetStringMenu(selected, this, selected));
            }
        }
        if (button.id == 103) { // Delete
            String selected = mapList.getSelected();
            if (selected != null && !selected.isEmpty()) {
                timerManager.removeMap(selected);
                this.mapList.refreshList();
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.mapList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.mapList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void onString(String string) {
        timerManager.addMap(string);
        this.mapList.refreshList();
        this.mapList.setSelected(string);
    }

    @Override
    public void onString(String string, Object object) {
        if (object instanceof String) {
            timerManager.renameMap((String) object, string);
            this.mapList.refreshList();
            this.mapList.setSelected(string);
        }
    }

    private class MapList extends GuiSlot {

        private final Minecraft mc;
        private List<String> maps;
        private String selected;

        public MapList(Minecraft mc) {
            super(mc, EditSpeedrunMapsMenu.this.width, EditSpeedrunMapsMenu.this.height,
                    40, EditSpeedrunMapsMenu.this.height - 60, 22);
            this.mc = mc;
            this.maps = timerManager.getMaps();
            this.selected = Option.SELECTED_SPEEDRUN_MAP.getStringValue();
        }

        public void refreshList() {
            this.maps = timerManager.getMaps();
        }

        public String getSelected() {
            return selected;
        }

        public void setSelected(String selected) {
            this.selected = selected;
            BLCSettings.updateOption(Option.SELECTED_SPEEDRUN_MAP, selected);
        }

        @Override
        protected int getSize() {
            return maps.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            if (maps.isEmpty()) return;
            setSelected(maps.get(index));
        }

        @Override
        protected boolean isSelected(int index) {
            return !selected.isEmpty() && selected.equals(maps.get(index));
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int index, int x, int y, int height, int mouseX, int mouseY) {
            String map = maps.get(index);
            int color = isSelected(index) ? 0xFFFF00 : 0xFFFFFF;

            mc.fontRendererObj.drawString(map, x + 5, y + 6, color);

            mc.fontRendererObj.drawString("≡", x + width - 20, y + 6, 0x666666);
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            if (mouseButton == 0) { // Left click
                int slotIndex = getSlotIndexFromScreenCoords(mouseX, mouseY);
                if (slotIndex >= 0 && slotIndex < maps.size()) {
                    selected = maps.get(slotIndex);
                }
            }
        }

        public int getSlotIndexFromScreenCoords(int mouseX, int mouseY) {
            int listLeft = this.left + this.width / 2 - this.getListWidth() / 2;
            int listRight = this.left + this.width / 2 + this.getListWidth() / 2;

            int relativeY = mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
            int index = relativeY / this.slotHeight;

            if (index >= 0 && index < this.getSize() &&
                    mouseX >= listLeft && mouseX <= listRight) {

                int slotTop = getSlotYFromIndex(index);
                int slotBottom = slotTop + this.slotHeight;
                if (mouseY >= slotTop && mouseY <= slotBottom) {
                    return index;
                }
            }
            return -1;
        }

        public int getSlotYFromIndex(int index) {
            return (int) (top - amountScrolled + index * slotHeight + 4);
        }
    }
}
