package de.kyrohpaneup.betterlinkcraft.gui.impl.chat;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.managers.AutoTextManager;
import de.kyrohpaneup.betterlinkcraft.managers.ConfigManager;
import de.kyrohpaneup.betterlinkcraft.mods.autotext.AutoText;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoTextMenu extends GuiScreen {

    private final GuiScreen parentScreen;
    private final AutoTextManager autoTextManager;
    private final ConfigManager configManager;

    private final List<AutoTextRow> autoTextRows = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int ROW_HEIGHT = 30;
    private static final int ROWS_VISIBLE = 10;

    private boolean listeningForKey = false;
    private AutoTextRow listeningRow = null;

    public AutoTextMenu(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.autoTextManager = BetterLinkCraft.INSTANCE.getAutoTextManager();
        this.configManager = BetterLinkCraft.INSTANCE.getConfigManager();
    }

    @Override
    public void initGui() {
        super.initGui();

        autoTextRows.clear();
        for (AutoText autoText : autoTextManager.getAutoTexts()) {
            autoTextRows.add(new AutoTextRow(autoText));
        }

        // Buttons
        int buttonWidth = 100;
        int buttonX = this.width / 2 - buttonWidth / 2;
        GuiButton addButton = new GuiButton(100, buttonX, 20, buttonWidth, 20, "Add New");
        GuiButton doneButton = new GuiButton(101, buttonX, this.height - 30, buttonWidth, 20, "Done");

        this.buttonList.add(addButton);
        this.buttonList.add(doneButton);

        if (autoTextRows.size() > ROWS_VISIBLE) {
            this.buttonList.add(new GuiButton(102, this.width - 30, 60, 20, 20, "↑"));
            this.buttonList.add(new GuiButton(103, this.width - 30, this.height - 40, 20, 20, "↓"));
        }

        updateRowPositions();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(this.fontRendererObj, "AutoText Manager", this.width / 2, 5, 0xFFFFFF);

        int tableY = 50;
        drawString(this.fontRendererObj, "Name", 50, tableY, 0xFFFF55);
        drawString(this.fontRendererObj, "Key", 200, tableY, 0xFFFF55);
        drawString(this.fontRendererObj, "Output", 280, tableY, 0xFFFF55);
        drawString(this.fontRendererObj, "Actions", 450, tableY, 0xFFFF55);

        drawHorizontalLine(40, this.width - 40, tableY + 10, 0xFFAAAAAA);

        for (int i = scrollOffset; i < Math.min(scrollOffset + ROWS_VISIBLE, autoTextRows.size()); i++) {
            AutoTextRow row = autoTextRows.get(i);
            if (row != null) {
                row.draw(mouseX, mouseY);
            }
        }

        if (listeningForKey && listeningRow != null) {
            drawCenteredString(this.fontRendererObj, "\u00A7aPress any key for: " + listeningRow.autoText.getName(), this.width / 2, this.height - 50, 0xFFFF55);
        }

        if (autoTextRows.size() > ROWS_VISIBLE) {
            String scrollInfo = "Showing " + (scrollOffset + 1) + "-" + Math.min(scrollOffset + ROWS_VISIBLE, autoTextRows.size()) + " of " + autoTextRows.size();
            drawString(this.fontRendererObj, scrollInfo, this.width - 120, this.height - 20, 0xAAAAAA);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (listeningForKey) {
            if (keyCode == Keyboard.KEY_ESCAPE) {
                setKeyForRow(0);
            } else if (keyCode != Keyboard.KEY_RETURN) {
                setKeyForRow(keyCode);
            }
            return;
        }

        if (keyCode == Keyboard.KEY_ESCAPE) {
            saveAndClose();
            return;
        }

        super.keyTyped(typedChar, keyCode);

        for (AutoTextRow row : autoTextRows) {
            row.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (int i = scrollOffset; i < Math.min(scrollOffset + ROWS_VISIBLE, autoTextRows.size()); i++) {
            AutoTextRow row = autoTextRows.get(i);
            if (row != null) {
                row.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 100: // Add New
                addNewAutoText();
                break;

            case 101: // Done
                saveAndClose();
                this.mc.displayGuiScreen(parentScreen);
                break;

            case 102: // Scroll Up
                scrollUp();
                break;

            case 103: // Scroll Down
                scrollDown();
                break;
        }
    }

    private void updateRowPositions() {
        int startY = 65;
        for (int i = 0; i < autoTextRows.size(); i++) {
            AutoTextRow row = autoTextRows.get(i);
            if (row != null) {
                row.setPosition(40, startY + (i - scrollOffset) * ROW_HEIGHT, this.width - 80);
            }
        }
    }

    private void addNewAutoText() {
        AutoText newAutoText = new AutoText("New AutoText", Keyboard.KEY_NONE, "");
        autoTextRows.add(new AutoTextRow(newAutoText));
        updateRowPositions();
    }

    private void removeAutoText(AutoTextRow row) {
        autoTextRows.remove(row);
        updateRowPositions();
    }

    private void startKeyListening(AutoTextRow row) {
        listeningForKey = true;
        listeningRow = row;
    }

    private void setKeyForRow(int keyCode) {
        if (listeningRow != null) {
            listeningRow.autoText.setKey(keyCode);
            listeningRow.updateKeyButton();
        }

        listeningForKey = false;
        listeningRow = null;
    }

    private void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset--;
            updateRowPositions();
        }
    }

    private void scrollDown() {
        if (scrollOffset < autoTextRows.size() - ROWS_VISIBLE) {
            scrollOffset++;
            updateRowPositions();
        }
    }

    private void saveAndClose() {
        for (AutoText existing : autoTextManager.getAutoTexts()) {
            autoTextManager.removeAutoText(existing.getName());
        }

        for (AutoTextRow row : autoTextRows) {
            try {
                autoTextManager.addAutoText(row.autoText);
            } catch (Exception e) {
                System.err.println("Error saving AutoText: " + e.getMessage());
            }
        }

        configManager.saveAutoTexts();
        this.mc.displayGuiScreen(parentScreen);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int scroll = org.lwjgl.input.Mouse.getEventDWheel();
        if (scroll != 0) {
            if (scroll > 0) {
                scrollUp();
            } else {
                scrollDown();
            }
        }
    }

    private class AutoTextRow {
        private final AutoText autoText;
        private GuiTextField nameField;
        private GuiButton keyButton;
        private GuiTextField outputField;
        private GuiButton testButton;
        private GuiButton deleteButton;

        private int x, y, width;

        public AutoTextRow(AutoText autoText) {
            this.autoText = autoText;
        }

        public void setPosition(int x, int y, int width) {
            this.x = x;
            this.y = y;
            this.width = width;

            int fieldWidth = 120;

            if (nameField == null) {
                nameField = new GuiTextField(0, fontRendererObj, x, y, fieldWidth, 16);
                nameField.setMaxStringLength(50);
                nameField.setText(autoText.getName());
            } else {
                nameField.xPosition = x;
                nameField.yPosition = y;
            }

            if (keyButton == null) {
                keyButton = new GuiButton(0, x + fieldWidth + 10, y, 60, 16, "");
                updateKeyButton();
            } else {
                keyButton.xPosition = x + fieldWidth + 10;
                keyButton.yPosition = y;
            }

            if (outputField == null) {
                outputField = new GuiTextField(0, fontRendererObj, x + fieldWidth + 80, y, fieldWidth + 40, 16);
                outputField.setMaxStringLength(256);
                outputField.setText(autoText.getOutput());
            } else {
                outputField.xPosition = x + fieldWidth + 80;
                outputField.yPosition = y;
            }

            if (testButton == null) {
                testButton = new GuiButton(0, x + width - 90, y, 40, 16, "Test");
            } else {
                testButton.xPosition = x + width - 90;
                testButton.yPosition = y;
            }

            if (deleteButton == null) {
                deleteButton = new GuiButton(0, x + width - 40, y, 40, 16, "Delete");
            } else {
                deleteButton.xPosition = x + width - 40;
                deleteButton.yPosition = y;
            }
        }

        public void draw(int mouseX, int mouseY) {
            if ((autoTextRows.indexOf(this) % 2) == 0) {
                drawRect(x - 5, y - 2, x + width + 5, y + ROW_HEIGHT - 8, 0x20FFFFFF);
            }

            nameField.drawTextBox();
            outputField.drawTextBox();

            drawButton(keyButton, mouseX, mouseY);
            drawButton(testButton, mouseX, mouseY);
            drawButton(deleteButton, mouseX, mouseY);
        }

        private void drawButton(GuiButton button, int mouseX, int mouseY) {
            boolean hovered = mouseX >= button.xPosition && mouseX < button.xPosition + button.width &&
                    mouseY >= button.yPosition && mouseY < button.yPosition + button.height;

            int color = hovered ? 0xFF555555 : 0xFF333333;
            drawRect(button.xPosition, button.yPosition, button.xPosition + button.width, button.yPosition + button.height, color);
            drawRect(button.xPosition + 1, button.yPosition + 1, button.xPosition + button.width - 1, button.yPosition + button.height - 1, 0xFF666666);

            int textColor = hovered ? 0xFFFFFF : 0xCCCCCC;
            drawCenteredString(fontRendererObj, button.displayString,
                    button.xPosition + button.width / 2,
                    button.yPosition + (button.height - 8) / 2,
                    textColor);
        }

        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            nameField.mouseClicked(mouseX, mouseY, mouseButton);
            outputField.mouseClicked(mouseX, mouseY, mouseButton);

            if (isMouseOver(keyButton, mouseX, mouseY)) {
                startKeyListening(this);
            } else if (isMouseOver(testButton, mouseX, mouseY)) {
                autoText.execute();
            } else if (isMouseOver(deleteButton, mouseX, mouseY)) {
                removeAutoText(this);
            }
        }

        public void keyTyped(char typedChar, int keyCode) {
            nameField.textboxKeyTyped(typedChar, keyCode);
            outputField.textboxKeyTyped(typedChar, keyCode);

            if (!nameField.getText().equals(autoText.getName())) {
                autoText.setName(nameField.getText());
            }
            if (!outputField.getText().equals(autoText.getOutput())) {
                autoText.setOutput(outputField.getText());
            }
        }

        private boolean isMouseOver(GuiButton button, int mouseX, int mouseY) {
            return mouseX >= button.xPosition && mouseX < button.xPosition + button.width &&
                    mouseY >= button.yPosition && mouseY < button.yPosition + button.height;
        }

        public void updateKeyButton() {
            if (autoText.getKey() == Keyboard.KEY_NONE || autoText.getKey() == 0) {
                keyButton.displayString = "NONE";
            } else {
                keyButton.displayString = autoText.getKey() == 0 ? "NONE" : Keyboard.getKeyName(autoText.getKey());
            }
        }
    }
}