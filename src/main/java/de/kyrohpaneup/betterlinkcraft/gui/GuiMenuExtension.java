package de.kyrohpaneup.betterlinkcraft.gui;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.gui.impl.BLCControlPresetMenu;
import de.kyrohpaneup.betterlinkcraft.gui.impl.BLCOptions;
import de.kyrohpaneup.betterlinkcraft.managers.ConfigManager;
import de.kyrohpaneup.betterlinkcraft.managers.KeybindManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GuiMenuExtension {

    private final int guiIngameMenuButton1Id = 1000;
    private final int guiControlsPresetsButton1Id = 1001;

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiIngameMenu) {
            int margin = 4;
            event.buttonList.add(new GuiButton(
                    guiIngameMenuButton1Id,
                    margin,
                    margin,
                    98,
                    20,
                    "BetterLC"
            ));
        } else if (event.gui instanceof GuiControls) {
            int i = 3;
            event.buttonList.add(new GuiButton(
                    guiControlsPresetsButton1Id,
                    event.gui.width / 2 - 155 + i % 2 * 160,
                    18 + 24 * (i >> 1),
                    150,
                    20,
                    "Presets"
                    ));
        }
    }


    @SubscribeEvent
    public void onAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.gui instanceof GuiIngameMenu) {
            if (event.button.id == guiIngameMenuButton1Id) {
                Minecraft.getMinecraft().displayGuiScreen(new BLCOptions(event.gui));
                event.setCanceled(true);
            }
        }
        if (event.gui instanceof GuiControls) {
            if (event.button.id == guiControlsPresetsButton1Id) {
                Minecraft.getMinecraft().displayGuiScreen(new BLCControlPresetMenu(event.gui));
                event.setCanceled(true);
            }
        }
    }

    boolean wasControlsOpen = false;

    @SubscribeEvent
    public void onGuiClosed(GuiOpenEvent event) {
        if (event.gui instanceof GuiControls) {
            wasControlsOpen = true;
        }
        else if (event.gui == null && wasControlsOpen) {
            wasControlsOpen = false;
            ConfigManager configManager = BetterLinkCraft.INSTANCE.getConfigManager();
            configManager.updatePreset(KeybindManager.selectedPreset);
        }
    }
}
