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

public class EditSpeedrunSegmentMenu extends StringGui {

    private final GuiScreen parentScreen;
    private SegmentList segmentList;
    private final TimerManager timerManager;
    private final ConfigManager configManager;

    public EditSpeedrunSegmentMenu(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.timerManager = BetterLinkCraft.INSTANCE.getTimerManager();
        this.configManager = BetterLinkCraft.INSTANCE.getConfigManager();
    }

    @Override
    public void initGui() {
        super.initGui();

        this.segmentList = new SegmentList(this.mc);

        int buttonWidth = 200 / 3;
        int bottomY = this.height - 28;

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(101, this.width / 2 - 100, bottomY - 25, buttonWidth, 20, "Create"));
        this.buttonList.add(new GuiButton(102, this.width / 2 - 100 + buttonWidth + 1, bottomY - 25, buttonWidth, 20, "Rename"));
        this.buttonList.add(new GuiButton(103, this.width / 2 - 100 + (buttonWidth * 2) + 2, bottomY - 25, buttonWidth, 20, "Delete"));
        this.buttonList.add(new GuiButton(100, this.width / 2 - 100, bottomY, 200, 20, "Done"));

        this.buttonList.add(new GuiButton(104, this.width / 2 - 100 + 200 + 10, bottomY, buttonWidth, 20, "Change Map"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        this.segmentList.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(this.fontRendererObj, "Speedrun Segments - " + Option.SELECTED_SPEEDRUN_MAP.getStringValue(), this.width / 2, 15, 0xFFFFFF);
        drawCenteredString(this.fontRendererObj, "Click and hold to drag segments", this.width / 2, 30, 0xAAAAAA);

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
            String selected = segmentList.getSelected();
            if (selected != null && !selected.isEmpty()) {
                this.mc.displayGuiScreen(new BLCSetStringMenu(selected, this, selected));
            }
        }
        if (button.id == 103) { // Delete
            String selected = segmentList.getSelected();
            if (selected != null && !selected.isEmpty()) {
                timerManager.removeSegment(selected);
                this.segmentList.refreshList();
            }
        }
        if (button.id == 104) { // Edit Maps
            this.mc.displayGuiScreen(new EditSpeedrunMapsMenu(this));
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.segmentList.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.segmentList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.segmentList.mouseReleased(mouseX, mouseY);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void onString(String string) {
        timerManager.addSegment(string);
        this.segmentList.refreshList();
        this.segmentList.setSelected(string);
    }

    @Override
    public void onString(String string, Object object) {
        if (object instanceof String) {
            timerManager.removeSegment((String) object);
            timerManager.addSegment(string);
            this.segmentList.refreshList();
            this.segmentList.setSelected(string);
        }
    }

    private class SegmentList extends GuiSlot {

        private final Minecraft mc;
        private List<String> segments;
        private String selected = "";
        private int dragIndex = -1;
        private int mouseYOffset = 0;
        private boolean isDragging = false;

        public SegmentList(Minecraft mc) {
            super(mc, EditSpeedrunSegmentMenu.this.width, EditSpeedrunSegmentMenu.this.height,
                    40, EditSpeedrunSegmentMenu.this.height - 60, 22);
            this.mc = mc;
            this.segments = timerManager.getSegments();
        }

        public void refreshList() {
            this.segments = timerManager.getSegments();
        }

        public String getSelected() {
            return selected;
        }

        public void setSelected(String selected) {
            this.selected = selected;
        }

        @Override
        protected int getSize() {
            return segments.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            if (segments.isEmpty()) return;
            selected = segments.get(index);
        }

        @Override
        protected boolean isSelected(int index) {
            return !selected.isEmpty() && selected.equals(segments.get(index));
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int index, int x, int y, int height, int mouseX, int mouseY) {
            String segment = segments.get(index);
            int color = isSelected(index) ? 0xFFFF00 : 0xFFFFFF;

            if (isDragging && dragIndex == index) {
                color = 0xAAAAAA;
            }

            mc.fontRendererObj.drawString(segment, x + 5, y + 6, color);

            mc.fontRendererObj.drawString("≡", x + width - 20, y + 6, 0x666666);
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            super.drawScreen(mouseX, mouseY, partialTicks);

            if (isDragging && dragIndex >= 0 && dragIndex < segments.size()) {
                String draggedSegment = segments.get(dragIndex);
                int yPos = mouseY - mouseYOffset;

                drawRect(width / 2 - 110, yPos, width / 2 + 110, yPos + 20, 0x44FFFFFF);
                drawCenteredString(mc.fontRendererObj, draggedSegment + " (moving...)", width / 2, yPos + 6, 0xFFFFFF);
            }
        }

        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            if (mouseButton == 0) { // Left click
                int slotIndex = getSlotIndexFromScreenCoords(mouseX, mouseY);
                if (slotIndex >= 0 && slotIndex < segments.size()) {
                    selected = segments.get(slotIndex);
                    dragIndex = slotIndex;
                    mouseYOffset = mouseY - getSlotYFromIndex(slotIndex);
                    isDragging = true;
                }
            }
        }

        public void mouseReleased(int mouseX, int mouseY) {
            if (isDragging && dragIndex >= 0) {
                int targetIndex = getSlotIndexFromScreenCoords(mouseX, mouseY);
                if (targetIndex >= 0 && targetIndex != dragIndex) {
                    moveSegmentToPosition(dragIndex, targetIndex);
                }

                isDragging = false;
                dragIndex = -1;
            }
        }

        private void moveSegmentToPosition(int fromIndex, int toIndex) {
            if (fromIndex >= 0 && fromIndex < segments.size() &&
                    toIndex >= 0 && toIndex < segments.size()) {
                String segment = segments.remove(fromIndex);
                segments.add(toIndex, segment);
                selected = segment;
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