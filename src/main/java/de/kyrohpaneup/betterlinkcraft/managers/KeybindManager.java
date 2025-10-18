package de.kyrohpaneup.betterlinkcraft.managers;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.keybinds.BLCKeybind;
import de.kyrohpaneup.betterlinkcraft.keybinds.controls.ControlPreset;
import de.kyrohpaneup.betterlinkcraft.keybinds.impl.*;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.util.*;

public class KeybindManager {
    public static List<BLCKeybind> keybinds = new ArrayList<>();
    public static List<ControlPreset> presets = new ArrayList<>();
    public static ControlPreset selectedPreset;
    private ConfigManager configManager = null;

    public void init() {
        this.configManager = BetterLinkCraft.INSTANCE.getConfigManager();
    }

    public void registerKeybindings() {
        keybinds.add(new ChangeSensKeybind());
        keybinds.add(new SwitchToUgisKey());
        keybinds.add(new OpenControlPresetsKey());
        keybinds.add(new OpenSheetKey());
        keybinds.add(new CreateStratReminderKey());
        keybinds.add(new EditStratReminderKey());
        keybinds.add(new ShowStratRemindersKey());
        keybinds.add(new StartTimerKey());
        keybinds.add(new StopTimerKey());
        keybinds.add(new OpenBLCSettingsKey());

        for (BLCKeybind keybind : keybinds) {
            ClientRegistry.registerKeyBinding(keybind.getKeyBinding());
        }
    }

    private final Map<BLCKeybind, Boolean> keyStates = new HashMap<>();

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        for (BLCKeybind keybind : KeybindManager.keybinds) {
            boolean isCurrentlyPressed = keybind.getKeyBinding().isKeyDown();
            boolean wasPreviouslyPressed = keyStates.getOrDefault(keybind, false);

            if (isCurrentlyPressed && !wasPreviouslyPressed) {
                keybind.onPressed();
            }

            else if (!isCurrentlyPressed && wasPreviouslyPressed) {
                keybind.onReleased();
            }

            if (isCurrentlyPressed) {
                keybind.onKeyDown();
            }

            keyStates.put(keybind, isCurrentlyPressed);
        }
    }

    public void applyPreset(ControlPreset preset) {
        if (selectedPreset != null && !preset.getName().equalsIgnoreCase(selectedPreset.getName())) {
            configManager.updatePreset(selectedPreset);
        }
        selectedPreset = preset;
        for (KeyBinding kb : Minecraft.getMinecraft().gameSettings.keyBindings) {
            if (preset.getBindings().containsKey(kb.getKeyDescription())) {
                kb.setKeyCode(preset.getBindings().get(kb.getKeyDescription()));
            }
        }
        Minecraft.getMinecraft().gameSettings.mouseSensitivity = preset.getSensitivity();
        KeyBinding.resetKeyBindingArrayAndHash();

        BLCSettings.updateOption(Option.SELECTED_PRESET, preset.getName());
    }
}
