package de.kyrohpaneup.betterlinkcraft.gui.impl.options;

import de.kyrohpaneup.betterlinkcraft.gui.StringGui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;

public class BLCSetStringMenu extends GuiScreen {

    String string;
    Object object = null;
    private final StringGui parent;
    private GuiTextField textField;
    int bX;
    int bY;
    int bWidth;
    int bHeight;

    public BLCSetStringMenu(String string, StringGui parent, Object object) {
        this.string = string;
        this.parent = parent;
        this.object = object;
    }

    public BLCSetStringMenu(String string, StringGui parent) {
        this.string = string;
        this.parent = parent;
    }

    @Override
    public void initGui() {
        super.initGui();
        calculateCoords();
        this.buttonList.add(new GuiButton(1, bX, bY + 40, bWidth, bHeight, "Done"));
        this.textField = new GuiTextField(0, fontRendererObj, bX, bY, bWidth, bHeight);
        this.textField.setText(string);
        this.textField.setMaxStringLength(50);
        this.textField.setFocused(true);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.textField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.textField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.textField.setFocused(true);
        this.textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public String getText() {
        return this.textField.getText();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 1) {
            if (object != null) {
                this.parent.onString(getText(), object);
            } else {
                this.parent.onString(getText());
            }
            this.mc.displayGuiScreen(this.parent);
        }
    }

    private void calculateCoords() {
        this.bWidth = width / 5;
        this.bX = width / 2 - bWidth / 2;
        this.bHeight = 20;
        this.bY = height / 3;
    }
}
