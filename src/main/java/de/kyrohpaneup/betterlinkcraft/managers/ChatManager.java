package de.kyrohpaneup.betterlinkcraft.managers;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatManager {

    public static final String prefix = "\u00A73\u00A7l\u3010\u00A7b\u00A7lBLC\u00A73\u00A7l\u3011-\u00A7r ";

    public static void sendMessage(String message) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }

    public static void sendMessageWithPrefix(String message) {
        sendMessage(prefix + " " + message);
    }
}
