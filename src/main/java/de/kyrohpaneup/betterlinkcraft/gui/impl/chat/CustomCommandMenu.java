package de.kyrohpaneup.betterlinkcraft.gui.impl.chat;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.managers.ConfigManager;
import de.kyrohpaneup.betterlinkcraft.managers.CustomCommandManager;
import de.kyrohpaneup.betterlinkcraft.mods.autotext.CustomCommand;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomCommandMenu extends GuiScreen {

    private final GuiScreen parentScreen;
    private final CustomCommandManager commandManager;
    private final ConfigManager configManager;

    private final List<CommandRow> commandRows = new ArrayList<>();
    private int scrollOffset = 0;
    private static final int ROW_HEIGHT = 30;
    private static final int ROWS_VISIBLE = 10;

    public CustomCommandMenu(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.commandManager = BetterLinkCraft.INSTANCE.getCustomCommandManager();
        this.configManager = BetterLinkCraft.INSTANCE.getConfigManager();
    }

    @Override
    public void initGui() {
        super.initGui();

        commandRows.clear();
        for (CustomCommand command : commandManager.getCustomCommands()) {
            commandRows.add(new CommandRow(command));
        }

        // Buttons
        int buttonWidth = 100;
        int buttonX = this.width / 2 - buttonWidth / 2;
        GuiButton addButton = new GuiButton(100, buttonX, 20, buttonWidth, 20, "Add New");
        GuiButton doneButton = new GuiButton(101, buttonX, this.height - 30, buttonWidth, 20, "Done");

        this.buttonList.add(addButton);
        this.buttonList.add(doneButton);

        if (commandRows.size() > ROWS_VISIBLE) {
            this.buttonList.add(new GuiButton(102, this.width - 30, 60, 20, 20, "↑"));
            this.buttonList.add(new GuiButton(103, this.width - 30, this.height - 40, 20, 20, "↓"));
        }

        updateRowPositions();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(this.fontRendererObj, "Custom Command Manager", this.width / 2, 5, 0xFFFFFF);

        int tableY = 50;
        drawString(this.fontRendererObj, "Name", 50, tableY, 0xFFFF55);
        drawString(this.fontRendererObj, "Command", 200, tableY, 0xFFFF55);
        drawString(this.fontRendererObj, "Output", 350, tableY, 0xFFFF55);
        drawString(this.fontRendererObj, "Actions", 500, tableY, 0xFFFF55);

        drawHorizontalLine(40, this.width - 40, tableY + 10, 0xFFAAAAAA);

        for (int i = scrollOffset; i < Math.min(scrollOffset + ROWS_VISIBLE, commandRows.size()); i++) {
            CommandRow row = commandRows.get(i);
            if (row != null) {
                row.draw(mouseX, mouseY);
            }
        }

        if (commandRows.size() > ROWS_VISIBLE) {
            String scrollInfo = "Showing " + (scrollOffset + 1) + "-" + Math.min(scrollOffset + ROWS_VISIBLE, commandRows.size()) + " of " + commandRows.size();
            drawString(this.fontRendererObj, scrollInfo, this.width - 120, this.height - 20, 0xAAAAAA);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            saveAndClose();
            return;
        }

        super.keyTyped(typedChar, keyCode);

        for (CommandRow row : commandRows) {
            row.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (int i = scrollOffset; i < Math.min(scrollOffset + ROWS_VISIBLE, commandRows.size()); i++) {
            CommandRow row = commandRows.get(i);
            if (row != null) {
                row.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 100: // Add New
                addNewCommand();
                break;

            case 101: // Done
                saveAndClose();
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
        for (int i = 0; i < commandRows.size(); i++) {
            CommandRow row = commandRows.get(i);
            if (row != null) {
                row.setPosition(40, startY + (i - scrollOffset) * ROW_HEIGHT, this.width - 80);
            }
        }
    }

    private void addNewCommand() {
        CustomCommand newCommand = new CustomCommand("New Command", "cmd", "Hello World");
        commandRows.add(new CommandRow(newCommand));
        updateRowPositions();
    }

    private void removeCommand(CommandRow row) {
        commandManager.removeCustomCommand(row.command.getName());
        commandRows.remove(row);
        updateRowPositions();
    }

    private void scrollUp() {
        if (scrollOffset > 0) {
            scrollOffset--;
            updateRowPositions();
        }
    }

    private void scrollDown() {
        if (scrollOffset < commandRows.size() - ROWS_VISIBLE) {
            scrollOffset++;
            updateRowPositions();
        }
    }

    private void saveAndClose() {
        // Komplett neue Command-Liste erstellen
        List<CustomCommand> newCommands = new ArrayList<>();
        for (CommandRow row : commandRows) {
            newCommands.add(row.command);
        }

        // Alte Commands komplett ersetzen
        commandManager.setCustomCommands(newCommands);

        configManager.saveCustomCommands();
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

    private class CommandRow {
        private final CustomCommand command;
        private GuiTextField nameField;
        private GuiTextField commandField;
        private GuiTextField outputField;
        private GuiButton testButton;
        private GuiButton deleteButton;

        private int x, y, width;

        public CommandRow(CustomCommand command) {
            this.command = command;
        }

        public void setPosition(int x, int y, int width) {
            this.x = x;
            this.y = y;
            this.width = width;

            int fieldWidth = 100;

            // Name Field
            if (nameField == null) {
                nameField = new GuiTextField(0, fontRendererObj, x, y, fieldWidth, 16);
                nameField.setMaxStringLength(50);
                nameField.setText(command.getName());
            } else {
                nameField.xPosition = x;
                nameField.yPosition = y;
            }

            // Command Field (z.B. "home" ohne /)
            if (commandField == null) {
                commandField = new GuiTextField(0, fontRendererObj, x + fieldWidth + 10, y, fieldWidth, 16);
                commandField.setMaxStringLength(50);
                commandField.setText(command.getCommand());
            } else {
                commandField.xPosition = x + fieldWidth + 10;
                commandField.yPosition = y;
            }

            // Output Field (was ausgegeben wird)
            if (outputField == null) {
                outputField = new GuiTextField(0, fontRendererObj, x + fieldWidth * 2 + 20, y, fieldWidth + 40, 16);
                outputField.setMaxStringLength(256);
                outputField.setText(command.getOutput());
            } else {
                outputField.xPosition = x + fieldWidth * 2 + 20;
                outputField.yPosition = y;
            }

            // Test Button
            if (testButton == null) {
                testButton = new GuiButton(0, x + width - 90, y, 40, 16, "Test");
            } else {
                testButton.xPosition = x + width - 90;
                testButton.yPosition = y;
            }

            // Delete Button
            if (deleteButton == null) {
                deleteButton = new GuiButton(0, x + width - 40, y, 40, 16, "Delete");
            } else {
                deleteButton.xPosition = x + width - 40;
                deleteButton.yPosition = y;
            }
        }

        public void draw(int mouseX, int mouseY) {
            if ((commandRows.indexOf(this) % 2) == 0) {
                drawRect(x - 5, y - 2, x + width + 5, y + ROW_HEIGHT - 8, 0x20FFFFFF);
            }

            nameField.drawTextBox();
            commandField.drawTextBox();
            outputField.drawTextBox();

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
            commandField.mouseClicked(mouseX, mouseY, mouseButton);
            outputField.mouseClicked(mouseX, mouseY, mouseButton);

            if (isMouseOver(testButton, mouseX, mouseY)) {
                command.execute();
            } else if (isMouseOver(deleteButton, mouseX, mouseY)) {
                removeCommand(this);
            }
        }

        public void keyTyped(char typedChar, int keyCode) {
            nameField.textboxKeyTyped(typedChar, keyCode);
            commandField.textboxKeyTyped(typedChar, keyCode);
            outputField.textboxKeyTyped(typedChar, keyCode);

            if (!nameField.getText().equals(command.getName())) {
                command.setName(nameField.getText());
            }
            if (!commandField.getText().equals(command.getCommand())) {
                command.setCommand(commandField.getText());
            }
            if (!outputField.getText().equals(command.getOutput())) {
                command.setOutput(outputField.getText());
            }
        }

        private boolean isMouseOver(GuiButton button, int mouseX, int mouseY) {
            return mouseX >= button.xPosition && mouseX < button.xPosition + button.width &&
                    mouseY >= button.yPosition && mouseY < button.yPosition + button.height;
        }
    }
}