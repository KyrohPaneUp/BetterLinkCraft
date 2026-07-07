package de.kyrohpaneup.betterlinkcraft.keybinds.impl;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import de.kyrohpaneup.betterlinkcraft.keybinds.controls.ControlPreset;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import de.kyrohpaneup.betterlinkcraft.managers.ConfigManager;
import de.kyrohpaneup.betterlinkcraft.managers.KeybindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovementInput;

public class ChangePresetKey extends BLCKeybind {

    ConfigManager configManager;
    KeybindManager keybindManager;

    public ChangePresetKey() {
        super("Switch Preset", 0, "BetterLinkCraft");
        configManager = BetterLinkCraft.INSTANCE.getConfigManager();
        keybindManager = BetterLinkCraft.INSTANCE.getKeybindManager();
    }

    @Override
    public void onPressed() {
        if (isMoving()) {
            ChatManager.sendMessageWithPrefix("§cPlease stop moving before doing this.");
            return;
        }

        ControlPreset selected = KeybindManager.selectedPreset;
        boolean isPrimary = selected.isPrimary();

        ControlPreset target = isPrimary ? configManager.getSecondary() : configManager.getPrimary();

        if (target == null) {
            String type = isPrimary ? "secondary" : "primary";
            ChatManager.sendMessageWithPrefix("§cYou need to define a " + type + " preset before doing this.");
        } else {
            keybindManager.applyPreset(target);
        }
    }

    @Override
    public void onReleased() {

    }

    @Override
    public void onKeyDown() {

    }

    private boolean isMoving() {
        MovementInput input = Minecraft.getMinecraft().thePlayer.movementInput;
        return input.moveForward != 0.0F || input.moveStrafe != 0.0F;
    }
}
