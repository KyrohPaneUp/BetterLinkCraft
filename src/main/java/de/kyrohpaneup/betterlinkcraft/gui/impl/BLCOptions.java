package de.kyrohpaneup.betterlinkcraft.gui.impl;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class BLCOptions extends GuiScreen {

    private final GuiScreen parentScreen;

    public BLCOptions(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = -16;
        int j = 98;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 24 + i, "General Settings"));
        //this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 48 + i, 98, 20, "Practice Settings"));
        //this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 4 + 48 + i, 98, 20, "Placeholder"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 48 + i, 98, 20, "StratReminders"));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, this.height / 4 + 48 + i, 98, 20, "Sheets"));
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 72 + i, 200, 20, "Chat Settings"));


        this.buttonList.add(new GuiButton(100, this.width / 2 - 100, this.height / 4 + 96 + (i / 2), 200, 20, "Done"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "BetterLC Options", this.width / 2, 40, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == 100) {
            // this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentScreen);
        }
        if (button.id == 0) {
            this.mc.displayGuiScreen(new BLCGeneralOptions(this));
        }
        if (button.id == 3) {
            this.mc.displayGuiScreen(new BLCStratReminderOptions(this));
        }
        if (button.id == 4) {
            this.mc.displayGuiScreen(new BLCSheetOptions(this));
        }
        if (button.id == 5) {
            this.mc.displayGuiScreen(new BLCChatOptions(this));
        }

    }
}
