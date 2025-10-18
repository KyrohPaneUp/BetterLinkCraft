package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class ChangeSensKeybind extends BLCKeybind {

    public ChangeSensKeybind() {
        super("Change Sensitivity", Keyboard.KEY_R, "BetterLinkCraft");
    }

    @Override
    public void onPressed() {
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            changeSens(true);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            changeSens(false);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            changeSensJump(false);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            changeSensJump(true);
        }
    }

    private void changeSens(boolean increase) {
        float sens = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
        float change = 0.1F;
        if (sens <= 0.5) {
            change = sens > 0.1F || sens == 0.15F && increase ? 0.05F : 0.025F;
        }
        sens = increase ?
                Math.min(1.0F, Math.round((sens + change) * 1000f) / 1000f) :
                Math.max(0.0F, Math.round((sens - change) * 1000f) / 1000f);
        Minecraft.getMinecraft().gameSettings.mouseSensitivity = sens;
        ChatManager.sendMessageWithPrefix("\u00A7aCurrent Sens: " + convertToPercent(sens) + "%");
    }

    private void changeSensJump(boolean right) {
        float sens = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
        if (!right) {
            sens = sens > 0.5F ? 0.5F : 0.0F;
        } else {
            sens = sens >= 0.5F ? 0.75F : 0.5F;
        }
        Minecraft.getMinecraft().gameSettings.mouseSensitivity = sens;
        ChatManager.sendMessageWithPrefix("\u00A7aCurrent Sens: " + convertToPercent(sens) + "%");
    }

    private int convertToPercent(float sens) {
        return Math.round(sens * 100 * 2);
    }

    @Override
    public void onReleased() {

    }

    @Override
    public void onKeyDown() {
        onPressed();
    }
}
