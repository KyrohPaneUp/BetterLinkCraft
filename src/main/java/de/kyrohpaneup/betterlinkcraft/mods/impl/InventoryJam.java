package de.kyrohpaneup.betterlinkcraft.mods.impl;

import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InventoryJam {

    Minecraft mc;

    public InventoryJam() {
        mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onGuiClose(GuiOpenEvent event) {
        if (Option.INV_JAM.getBooleanValue() && mc.thePlayer != null && event.gui == null) {
            waitForScreenNull();
        }
    }

    private void syncMovementKeys() {
        checkKey(mc.gameSettings.keyBindForward);
        checkKey(mc.gameSettings.keyBindBack);
        checkKey(mc.gameSettings.keyBindLeft);
        checkKey(mc.gameSettings.keyBindRight);
        checkKey(mc.gameSettings.keyBindJump);
        checkKey(mc.gameSettings.keyBindSprint);
    }

    private void checkKey(KeyBinding key) {
        if (mc.thePlayer == null || mc.theWorld == null || mc.currentScreen != null) return;
        int keyCode = key.getKeyCode();
        boolean isDown = keyCode < 0 ? Mouse.isButtonDown(keyCode) : Keyboard.isKeyDown(keyCode);
        KeyBinding.setKeyBindState(keyCode, isDown);
    }

    private void waitForScreenNull() {
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    if (mc.currentScreen == null) {
                        syncMovementKeys();
                        //mc.addScheduledTask(this::syncMovementKeys);
                        return;
                    }
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
