package de.kyrohpaneup.betterlinkcraft.gui.impl.sheet;

import de.kyrohpaneup.betterlinkcraft.managers.SheetManager;
import de.kyrohpaneup.betterlinkcraft.mods.sheet.GoogleSheetsAPI;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class BLCResetSheetMenu extends GuiScreen {

    private final GuiScreen parentScreen;

    public BLCResetSheetMenu(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        super.initGui();

        int buttonWidth = width / 4;
        int buttonHeight = 20;

        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, width / 2 - buttonWidth - 1, height / 2 - buttonHeight / 2, buttonWidth, buttonHeight, "Beginner Sheet"));
        this.buttonList.add(new GuiButton(2, width / 2 + 1, height / 2 - buttonHeight / 2, buttonWidth, buttonHeight, "Expert Sheet"));
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        drawDefaultBackground();
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);

        String title = "Reset Stratsheet Link";

        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(
                fontRendererObj,
                title,
                (this.width / 2) / 2,
                this.height / 5 - 20,
                Color.WHITE.getRGB()
        );
        GL11.glPopMatrix();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        if (button.id == 1) {
            BLCSettings.updateOption(Option.SHEET_LINK, GoogleSheetsAPI.BEGINNER_URL);
            SheetManager.reset();
            mc.displayGuiScreen(parentScreen);
        }

        if (button.id == 2) {
            BLCSettings.updateOption(Option.SHEET_LINK, GoogleSheetsAPI.EXPERT_URL);
            SheetManager.reset();
            mc.displayGuiScreen(parentScreen);
        }
    }
}
