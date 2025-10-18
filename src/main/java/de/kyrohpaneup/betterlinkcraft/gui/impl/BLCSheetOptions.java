package de.kyrohpaneup.betterlinkcraft.gui.impl;

import de.kyrohpaneup.betterlinkcraft.gui.elements.BLCOptionButton;
import de.kyrohpaneup.betterlinkcraft.gui.impl.sheet.BLCSheetListMenu;
import de.kyrohpaneup.betterlinkcraft.gui.impl.sheet.GuiSheetViewer;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import de.kyrohpaneup.betterlinkcraft.managers.SheetManager;
import de.kyrohpaneup.betterlinkcraft.mods.sheet.GoogleSheetsAPI;
import de.kyrohpaneup.betterlinkcraft.settings.BLCSettings;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import de.kyrohpaneup.betterlinkcraft.settings.OptionType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.net.URI;
import java.util.stream.IntStream;

public class BLCSheetOptions extends GuiScreen {

    private final String tagSheetUrl = "https://docs.google.com/spreadsheets/d/1DaA4u1ybNF0Vb50vu_X-XUrpdvf12pvKCoNucdV1XSc/edit";
    private final String stratSheetUrl = "https://docs.google.com/spreadsheets/d/1IJKfGsxa-vg3_EoVc8y5E0fek_bTSzawAaNMfFsfja0/edit?usp=sharing";

    private final GuiScreen parentScreen;
    private String title;

    public BLCSheetOptions(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.title = "Sheet Options";

        String[] labels = {"Tag Sheet", "Strat Sheet", "Sheet List", "Sheet Menu"};
        IntStream.range(0, labels.length).forEach(i ->
                this.buttonList.add(new GuiButton(
                        i + 1,
                        this.width / 2 - 155 + (i % 2) * 160,
                        this.height / 6 + 24 * (i / 2),
                        150, 20,
                        labels[i]
                ))
        );

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 120, "Done"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            int id = button.id;
            if (id < 100 && button instanceof BLCOptionButton) {
                BLCOptionButton optionButton = (BLCOptionButton) button;
                if (optionButton.getOption().getType() == OptionType.BOOLEAN || optionButton.getOption().getType() == OptionType.ENUM)
                    BLCSettings.switchOption(optionButton.getOption());
                optionButton.updateName(optionButton.getOption().getDisplayString());
            }

            if (id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
                return;
            }
            if (id == 1) {
                openLink(tagSheetUrl, "Tag Sheet");
                return;
            }
            if (id == 2) {
                openLink(stratSheetUrl, "Strat Sheet");
                return;
            }
            if (id == 3) {
                this.mc.displayGuiScreen(new BLCSheetListMenu(this));
            }
            if (id == 4) {
                if (SheetManager.DATA == null) {
                    SheetManager.DATA = GoogleSheetsAPI.getSheetData(Option.SELECTED_SHEET.getStringValue());
                }

                if (SheetManager.SHEET == null) {
                    SheetManager.SHEET = new GuiSheetViewer(SheetManager.DATA);
                } else if (SheetManager.SHEET.sheetData != SheetManager.DATA) {
                    SheetManager.SHEET = new GuiSheetViewer(SheetManager.DATA);
                }

                this.mc.displayGuiScreen(SheetManager.SHEET);
            }
        }
    }

    private void openLink(String url, String display) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            sendLinkToChat(url, display);
        }
    }

    private void sendLinkToChat(String url, String displayText) {
        if (mc.thePlayer == null) return;

        IChatComponent linkPart = new ChatComponentText(
                ChatManager.prefix + "\u00A7a Click here to open the " + displayText);

        ChatStyle style = new ChatStyle();
        style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        style.setChatHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ChatComponentText("\u00A7a" + url)
        ));
        linkPart.setChatStyle(style);

        mc.thePlayer.addChatMessage(linkPart);
    }
}
