package de.kyrohpaneup.betterlinkcraft.gui.impl;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.gui.StringGui;
import de.kyrohpaneup.betterlinkcraft.gui.impl.options.BLCSetStringMenu;
import de.kyrohpaneup.betterlinkcraft.keybinds.controls.ControlPreset;
import de.kyrohpaneup.betterlinkcraft.managers.ConfigManager;
import de.kyrohpaneup.betterlinkcraft.managers.KeybindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;

import java.io.IOException;
import java.util.List;

public class BLCControlPresetMenu extends StringGui {

    private final KeybindManager keybindManager = BetterLinkCraft.INSTANCE.getKeybindManager();
    private final ConfigManager configManager = BetterLinkCraft.INSTANCE.getConfigManager();
    private final GuiScreen parentScreen;
    private PresetList presetList;

    public BLCControlPresetMenu(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.presetList = new PresetList(this.mc);

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

        this.presetList.drawScreen(mouseX, mouseY, partialTicks);

        drawCenteredString(this.fontRendererObj, "Control Presets", this.width / 2, 15, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
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
            ControlPreset selected = KeybindManager.selectedPreset;
            if (selected != null) {
                this.mc.displayGuiScreen(new BLCSetStringMenu(selected.getName(), this, selected));
            }
        }
        if (button.id == 103) { // Delete
            ControlPreset selected = KeybindManager.selectedPreset;
            if (selected != null) {
                configManager.deletePreset(selected);
                this.presetList.refreshList();
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.presetList.handleMouseInput();
    }

    @Override
    public void onString(String string) {
        this.configManager.createPreset(string);
        this.presetList.refreshList();
    }

    @Override
    public void onString(String string, Object object) {
        if (object instanceof ControlPreset) {
            configManager.renamePreset((ControlPreset) object, string);
            this.presetList.refreshList();
        }
    }

    private class PresetList extends GuiSlot {

        private final Minecraft mc;
        private List<ControlPreset> presets;

        public PresetList(Minecraft mc) {
            super(mc, BLCControlPresetMenu.this.width, BLCControlPresetMenu.this.height,
                    40, BLCControlPresetMenu.this.height - 60, 22);
            this.mc = mc;
            this.presets = KeybindManager.presets;
        }

        public void refreshList() {
            this.presets = KeybindManager.presets;
        }

        @Override
        protected int getSize() {
            return presets.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            if (presets.isEmpty()) return;
            ControlPreset selected = presets.get(index);
            keybindManager.applyPreset(selected);
        }

        @Override
        protected boolean isSelected(int index) {
            return KeybindManager.selectedPreset.getName().equalsIgnoreCase(presets.get(index).getName());
        }

        @Override
        protected void drawBackground() {
            drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int index, int x, int y, int height, int mouseX, int mouseY) {
            ControlPreset preset = presets.get(index);
            String name = preset.getName();
            int color = 0xFFFFFF;

            mc.fontRendererObj.drawString(name, x + 5, y + 6, color);
        }
    }
}
