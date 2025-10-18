package de.kyrohpaneup.betterlinkcraft.gui.impl;

import de.kyrohpaneup.betterlinkcraft.gui.elements.BLCDoubleOptionSlider;
import de.kyrohpaneup.betterlinkcraft.gui.elements.BLCOptionButton;
import de.kyrohpaneup.betterlinkcraft.gui.elements.BLCOptionSlider;
import de.kyrohpaneup.betterlinkcraft.gui.impl.speedrun.EditSpeedrunSegmentMenu;
import de.kyrohpaneup.betterlinkcraft.gui.impl.speedrun.SpeedrunCompleteMenu;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import de.kyrohpaneup.betterlinkcraft.settings.OptionType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BLCGeneralOptions extends GuiScreen {

    private static final Option[] options = new Option[] {Option.UGIS_PRAC_DELAY, Option.INV_JAM, Option.FULLBRIGHT_ENABLED, Option.TIMER_ENABLED};
    private final GuiScreen parentScreen;
    private String title;

    public BLCGeneralOptions(GuiScreen parentScreenIn) {
        this.parentScreen = parentScreenIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        int i = 0;
        this.title = "General Options";

        for (Option blcsettings$options : options) {
            if (blcsettings$options.getType() == OptionType.FLOAT) {
                this.buttonList.add(new BLCOptionSlider(blcsettings$options.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), blcsettings$options, (Float) blcsettings$options.getMinValue(), (Float) blcsettings$options.getMaxValue()));
            } else if (blcsettings$options.getType() == OptionType.DOUBLE) {
                this.buttonList.add(new BLCDoubleOptionSlider(blcsettings$options.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), blcsettings$options, (double) blcsettings$options.getMinValue(), (double) blcsettings$options.getMaxValue()));
            }
            else {
                this.buttonList.add(new BLCOptionButton(blcsettings$options.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), blcsettings$options));
            }

            ++i;
        }
        this.buttonList.add(new GuiButton(201, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, "Speedrun Sections"));
        ++i;
        this.buttonList.add(new GuiButton(202, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, "Speedrun Results"));

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 120, "Done"));
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id < 100 && button instanceof BLCOptionButton) {
                BLCOptionButton optionButton = (BLCOptionButton) button;
                if (optionButton.getOption().getType() == OptionType.BOOLEAN || optionButton.getOption().getType() == OptionType.ENUM)
                    BLCSettings.switchOption(optionButton.getOption());
                optionButton.updateName(optionButton.getOption().getDisplayString());
            }

            if (button.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            }
            if (button.id == 201) {
                this.mc.displayGuiScreen(new EditSpeedrunSegmentMenu(this));
            }
            if (button.id == 202) {
                this.mc.displayGuiScreen(new SpeedrunCompleteMenu(null));
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
