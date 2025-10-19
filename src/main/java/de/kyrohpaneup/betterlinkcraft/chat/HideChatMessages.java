package de.kyrohpaneup.betterlinkcraft.chat;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import de.kyrohpaneup.betterlinkcraft.managers.CommandManager;
import de.kyrohpaneup.betterlinkcraft.managers.CustomCommandManager;
import de.kyrohpaneup.betterlinkcraft.managers.LinkCraftManager;
import de.kyrohpaneup.betterlinkcraft.mods.autotext.CustomCommand;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public class HideChatMessages {

    private CustomCommandManager commandManager;

    public HideChatMessages() {
        this.commandManager = BetterLinkCraft.INSTANCE.getCustomCommandManager();
    }
    
    @SubscribeEvent
    public void onMessage(ClientChatReceivedEvent event) {
        if (!LinkCraftManager.isOnLinkCraft()) return;
        String msg = event.message.getFormattedText();
        // Hide Practice Messages
        if (Option.HIDE_PRAC.getBooleanValue()) {
            if (msg.contains("You are on a practice mode, Do /unprac to stop.") || msg.contains("You have stopped practicing.")) {
                event.setCanceled(true);
                return;
            }
        }
        if (Option.HIDE_JOIN.getBooleanValue()) {
            if (msg.contains("--* * *--")) {
                event.setCanceled(true);
                return;
            }
        }
        if (Option.CLEAN_CHAT.getBooleanValue()) {
            if (msg.contains("\u00AB")) {
                String newMsg = getCleanMessage(msg);
                event.setCanceled(true);
                ChatManager.sendMessage(newMsg);
            }
        }
    }

    private String getCleanMessage(String text) {
        if (text.toLowerCase().contains("ragequit") || text.toLowerCase().contains("ranked")) {
            return text;
        }
        char faceLeft = '\u00AB';
        char faceRight = '\u00BB';

        if (text.indexOf(faceLeft) == -1 || text.indexOf(faceRight) == -1) return text;
        int lastFRChar = text.lastIndexOf(faceRight);
        int lastFLChar = text.substring(0, lastFRChar).lastIndexOf(faceLeft);
        int numberOfSymbols = getAmountOfCharacters(text.substring(lastFLChar, lastFRChar + 1), "\u00BB");
        int firstFLChar = findNthLastIndex(text.substring(0, lastFLChar + 1), faceLeft, numberOfSymbols - 1);
        if (firstFLChar == -1) {
            return text;
        }

        if (numberOfSymbols != (getAmountOfCharacters(text.substring(firstFLChar - 1, lastFRChar), "\u00AB") + 1)) {
            return text;
        }
        String beforeIndex = text.substring(0, firstFLChar);

        if (ignoreMessage(beforeIndex)) return text;

        int subIndex = beforeIndex.lastIndexOf('\u0020');
        if (subIndex == -1) {
            return text;
        }
        return text.substring(subIndex + 1);
    }

    private int findNthLastIndex(String str, char c, int n) {
        int index = str.length();
        for (int i = 0; i < n; i++) {
            index = str.lastIndexOf(c, index - 1);
            if (index == -1) {
                return -1; // not found
            }
        }
        return index;
    }

    private int getAmountOfCharacters(String text, String character) {
        return text.length() - text.replace(character, "").length();
    }

    private boolean ignoreMessage(String text) {
        return text.contains("Party") || text.contains("me ->");
    }
}

