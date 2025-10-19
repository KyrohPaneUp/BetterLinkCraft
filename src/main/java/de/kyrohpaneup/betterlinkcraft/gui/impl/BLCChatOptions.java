package de.kyrohpaneup.betterlinkcraft.gui.impl;

import de.kyrohpaneup.betterlinkcraft.gui.StringGui;
import de.kyrohpaneup.betterlinkcraft.gui.elements.BLCOptionButton;
import de.kyrohpaneup.betterlinkcraft.gui.elements.BLCOptionSlider;
import de.kyrohpaneup.betterlinkcraft.gui.impl.chat.AutoTextMenu;
import de.kyrohpaneup.betterlinkcraft.gui.impl.chat.CustomCommandMenu;
import de.kyrohpaneup.betterlinkcraft.gui.impl.options.BLCSetStringMenu;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import de.kyrohpaneup.betterlinkcraft.settings.OptionType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class BLCChatOptions extends StringGui
{
    private static final Option[] options = new Option[] {Option.HIDE_PRAC, Option.HIDE_JOIN, Option.CLEAN_CHAT, Option.GG_COLOR, Option.CUSTOM_GG_TEXT};
    private final GuiScreen parentScreen;
    private String title;

    public BLCChatOptions(GuiScreen parentScreenIn) {
        this.parentScreen = parentScreenIn;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        int i = 0;
        this.title = "Chat Options";

        for (Option blcsettings$options : options) {
            if (blcsettings$options.getType() == OptionType.FLOAT) {
                this.buttonList.add(new BLCOptionSlider(blcsettings$options.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), blcsettings$options));
            }
            else {
                this.buttonList.add(new BLCOptionButton(blcsettings$options.ordinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), blcsettings$options));
            }

            ++i;
        }
        this.buttonList.add(new GuiButton(201, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, "AutoText"));
        i++;
        this.buttonList.add(new GuiButton(202, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, "Custom Commands"));

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 120, "Done"));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id < 100 && button instanceof BLCOptionButton) {
                BLCOptionButton optionButton = (BLCOptionButton) button;
                Option option = optionButton.getOption();
                if (option.getType() == OptionType.BOOLEAN || option.getType() == OptionType.ENUM)
                    BLCSettings.switchOption(optionButton.getOption());
                if (option.getType() == OptionType.STRING) {
                    this.mc.displayGuiScreen(new BLCSetStringMenu(option.getStringValue(), this, option));
                    return;
                }
                optionButton.updateName(optionButton.getOption().getDisplayString());
            }

            if (button.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            }
            if (button.id == 201) {
                this.mc.displayGuiScreen(new AutoTextMenu(this));
            }
            if (button.id == 202) {
                this.mc.displayGuiScreen(new CustomCommandMenu(this));
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onString(String string, Object object) {
        if (!(object instanceof Option)) {
            return;
        }
        Option option = (Option) object;
        if (option.getType() == OptionType.STRING && string != null && !string.replaceAll(" ", "").equals("")) {
            BLCSettings.updateOption(option, string);
        }
    }
}