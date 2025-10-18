package de.kyrohpaneup.betterlinkcraft.gui.elements;

import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BLCOptionButton extends GuiButton {

    private final Option option;

    public BLCOptionButton(int buttonId, int x, int y, Option option) {
        super(buttonId, x, y, 150, 20, option.getDisplayString());
        this.option = option;
    }

    public void updateName(String newName) {
        this.displayString = newName;
    }

    public Option getOption() {
        return this.option;
    }
}

