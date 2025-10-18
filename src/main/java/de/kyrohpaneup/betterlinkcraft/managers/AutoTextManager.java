package de.kyrohpaneup.betterlinkcraft.managers;

import de.kyrohpaneup.betterlinkcraft.mods.autotext.AutoText;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoTextManager {

    private List<AutoText> autoTexts = new ArrayList<>();
    private final Map<Integer, Boolean> keyStates = new HashMap<>();

    public List<AutoText> getAutoTexts() {
        return new ArrayList<>(autoTexts);
    }

    public void removeAutoText(String name) {
        AutoText text = getAutoText(name);
        if (text != null) {
            autoTexts.remove(text);
        }
    }

    public AutoText getAutoText(String name) {
        for (AutoText text : autoTexts) {
            if (text.getName().equalsIgnoreCase(name)) return text;
        }
        return null;
    }

    public void setAutoTexts(List<AutoText> autoTexts) {
        this.autoTexts = autoTexts;
    }

    public void addAutoText(AutoText autoText) {
        if (getAutoText(autoText.getName()) != null) {
            throw new IllegalArgumentException("AutoText with name '" + autoText.getName() + "' already exists!");
        }

        this.autoTexts.add(autoText);
    }

    public void sendAutoText(AutoText autoText) {
        if (autoText != null && autoText.getOutput() != null && !autoText.getOutput().isEmpty()) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage(autoText.getOutput());
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        for (AutoText autoText : autoTexts) {
            int key = autoText.getKey();
            boolean isCurrentlyPressed = Keyboard.isKeyDown(key);
            boolean wasPreviouslyPressed = keyStates.getOrDefault(key, false);

            if (isCurrentlyPressed && !wasPreviouslyPressed) {
                sendAutoText(autoText);
            }

            keyStates.put(key, isCurrentlyPressed);
        }
    }
}