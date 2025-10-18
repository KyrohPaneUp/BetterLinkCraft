package de.kyrohpaneup.betterlinkcraft.mods.impl;

import net.minecraft.client.Minecraft;

public class FullBright {


    public static void enable() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.gammaSetting = 16.0F;
    }

    public static void disable() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.gameSettings.gammaSetting = 1.0F;
    }

}
