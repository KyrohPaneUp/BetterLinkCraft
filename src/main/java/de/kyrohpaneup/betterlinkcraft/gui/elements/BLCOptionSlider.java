package de.kyrohpaneup.betterlinkcraft.gui.elements;

import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BLCOptionSlider extends GuiButton {

    private float sliderValue;
    public boolean isDragging;

    private final Option option;

    public BLCOptionSlider(int buttonId, int x, int y, Option option) {
        super(buttonId, x, y, 150, 20, "");
        this.sliderValue = 1.0F;
        this.option = option;

        this.sliderValue = option.getFloatValue();
        this.displayString = option.getDisplayString();
    }

    @Override
    protected int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            if (this.isDragging) {
                this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);

                BLCSettings.updateOption(this.option, this.sliderValue);

                this.displayString = option.getDisplayString();
            }

            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            int sliderX = this.xPosition + (int) (this.sliderValue * (this.width - 8));
            this.drawTexturedModalRect(sliderX, this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(sliderX + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);

            BLCSettings.updateOption(this.option, this.sliderValue);
            this.displayString = option.getDisplayString();

            this.isDragging = true;
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.isDragging = false;
    }
}

