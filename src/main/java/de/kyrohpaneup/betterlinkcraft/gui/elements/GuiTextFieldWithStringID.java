package de.kyrohpaneup.betterlinkcraft.gui.elements;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldWithStringID extends GuiTextField {

    private String stringId = "";

    public GuiTextFieldWithStringID(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
        this.setMaxStringLength(50);
    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }
}
