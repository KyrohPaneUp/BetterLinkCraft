package de.kyrohpaneup.betterlinkcraft.gui.impl.strat;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.gui.elements.GuiTextFieldWithStringID;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import de.kyrohpaneup.betterlinkcraft.managers.StratReminderManager;
import de.kyrohpaneup.betterlinkcraft.mods.stratreminders.StratReminder;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CreateStratReminderGui extends GuiScreen {

    private final boolean isEditMenu;
    private int currentReminder = 0;
    private List<Map<String, String>> selectedMap;

    private StratReminder stratReminder;

    private Map<String, String> stratMap;

    StratReminderManager srm = BetterLinkCraft.INSTANCE.getSrm();

    public CreateStratReminderGui(Map<String, String> stratMap, boolean isEditMenu) {
        this.isEditMenu = isEditMenu;
        if (!isEditMenu) {
            Minecraft mc = Minecraft.getMinecraft();
            stratMap.put("x", String.valueOf((int) mc.thePlayer.posX));
            stratMap.put("y", String.valueOf((int) mc.thePlayer.posY));
            stratMap.put("z", String.valueOf((int) mc.thePlayer.posZ));
            this.stratMap = stratMap;
        } else {
            this.stratMap = stratMap;
            loadEditMenu();
        }
    }

    private void loadEditMenu() {
        selectedMap = srm.getAllRemindersMap();
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        selectedMap.sort(Comparator.comparingDouble(map -> {
            double x = Integer.parseInt(map.get("x"));
            double y = Integer.parseInt(map.get("y"));
            double z = Integer.parseInt(map.get("z"));
            return player.getDistance(x, y, z);
        }));
        if (!(selectedMap.size() < currentReminder + 1)) {
            this.stratMap.clear();
            this.stratMap = selectedMap.get(currentReminder);
        } else {
            if (mc != null && mc.currentScreen != null) {
                mc.displayGuiScreen(null);
            }
        }
        this.stratMap.put("map id", Option.SELECTED_SR_MAP.getStringValue());
    }

    private final List<GuiTextFieldWithStringID> textFields = new ArrayList<>();
    private final String[] fieldStrings = new String[] {
            "Position",
            "Facing",
            "Setup",
            "Strat",
            "Comment",
            "Map ID"
    };

    private int fieldWidth;
    private final int fieldHeight = 20;
    private int screenSize;
    private final int createButtonID = 0;
    private final int rightButton = 1;
    private final int leftButton = 2;
    private final int deleteButton = 3;
    private boolean init = false;
    private GuiTextFieldWithStringID selectedTextField;

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.textFields.clear();
        this.init = false;

        this.screenSize = this.width;
        fieldWidth = this.width / 10;

        int x = this.width / 2 - fieldWidth / 4;
        int y = this.height / 5;

        int buttonWidth = (int) (fieldWidth * 1.5);
        int buttonX = screenSize / 2 - buttonWidth / 2 + 2;
        int smallFieldWidth = buttonWidth / 3 - buttonWidth / 10;

        for (int i = 0; i < 9; i++) {
            if (i < 6) {
                GuiTextFieldWithStringID textField = new GuiTextFieldWithStringID(i, fontRendererObj, x, y, fieldWidth, fieldHeight);
                textField.setStringId(fieldStrings[i]);
                textFields.add(textField);
                y = y + 25;
            } else {
                int xCalc = i - 6;
                int xPos = getX(xCalc, smallFieldWidth, buttonX, x);
                GuiTextFieldWithStringID textField = new GuiTextFieldWithStringID(i, fontRendererObj, xPos, y, smallFieldWidth, fieldHeight);
                textField.setStringId(returnLetter(xCalc));
                textFields.add(textField);
            }
        }

        y = y + 25;
        String string = "Create";
        if (isEditMenu) string = "Save";
        GuiButton createButton = new GuiButton(createButtonID, screenSize / 2 - buttonWidth / 2 + 2, y, buttonWidth, fieldHeight, string);

        buttonList.clear();
        buttonList.add(createButton);
        if (isEditMenu) {
            GuiButton goLeftButton = new GuiButton(leftButton, createButton.xPosition, y + 25, createButton.height, createButton.height, "<-");
            GuiButton goRightButton = new GuiButton(rightButton, createButton.xPosition + createButton.getButtonWidth() - goLeftButton.getButtonWidth(), y + 25, createButton.height, createButton.height, "->");
            int delX = createButton.xPosition + (createButton.getButtonWidth() / 2) - (createButton.height / 2);
            GuiButton deleteButton = new GuiButton(this.deleteButton, delX, y + 25, createButton.height, createButton.height, "X");
            buttonList.add(goLeftButton);
            buttonList.add(goRightButton);
            buttonList.add(deleteButton);
            goLeftButton.enabled = !(currentReminder <= 0);
            goRightButton.enabled = !(currentReminder + 1 >= selectedMap.size());
        }

        super.initGui();
    }

    private int getX(int i, int width, int buttonX, int fieldX) {
        int returnValue = 0;
        switch (i) {
            case 0:
                returnValue = buttonX + 2;
                break;
            case 1:
                returnValue = screenSize / 2 - width / 2 + 2;
                break;
            case 2:
                returnValue = fieldX + fieldWidth - width;
        }
        return returnValue;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        drawDefaultBackground();

        // heading
        String title = "StratReminders";

        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);
        drawCenteredString(
                fontRendererObj,
                title,
                (this.width / 2) / 2,
                this.height / 15,
                Color.WHITE.getRGB()
        );
        GL11.glPopMatrix();

        int x = screenSize / 2;
        int minus;
        for (GuiTextFieldWithStringID field : textFields) {

            if (!init) {
                field.setText(stratMap.getOrDefault(field.getStringId().toLowerCase(), ""));
            }

            minus = fieldWidth / 4;

            if (selectedTextField == null) {
                selectedTextField = field;
            }
            if (fieldStrings.length > field.getId()) {
                String string = fieldStrings[field.getId()];
                drawString(fontRendererObj, string, x - (field.getWidth() / 2) - minus, field.yPosition + (fieldHeight / 2) - (fontRendererObj.FONT_HEIGHT / 2), Color.WHITE.getRGB());
            }

            field.drawTextBox();
            field.setFocused(field == selectedTextField);
        }
        init = true;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (GuiTextFieldWithStringID field : textFields) {
            boolean mouseOver = mouseX >= field.xPosition && mouseX < field.xPosition + field.width &&
                    mouseY >= field.yPosition && mouseY < field.yPosition + field.height;

            if (mouseOver) {
                selectedTextField = field;
                field.setCursorPositionEnd();
            }
            field.setFocused(mouseOver);

        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        if (selectedTextField != null) {

            if (keyCode == Keyboard.KEY_TAB || keyCode == Keyboard.KEY_RETURN) {
                int selected = selectedTextField.getId();

                if (selected == 8) {
                    selected = -1;
                }
                selectedTextField = textFields.get(selected + 1);
            }

            selectedTextField.textboxKeyTyped(typedChar, keyCode);
        }

        super.keyTyped(typedChar, keyCode);
    }

    private String returnLetter(int i) {
        switch (i) {
            case 0: return "x";
            case 1: return "y";
            case 2: return "z";
        }
        return "";
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case createButtonID:
                saveReminder();
                break;
            case rightButton:
                nextReminder(true);
                break;
            case leftButton:
                nextReminder(false);
                break;
            case deleteButton:
                mc.displayGuiScreen(null);
                deleteReminder();
                break;
        }
    }

    private void saveReminder() {
        String position = getTextFieldValue("Position");
        String facing = getTextFieldValue("Facing");
        String setup = getTextFieldValue("Setup");
        String strat = getTextFieldValue("Strat");
        String comment = getTextFieldValue("Comment");
        String mapId = getTextFieldValue("Map ID");

        if (mapId == null || mapId.replace(" ", "").equals("")) {
            ChatManager.sendMessageWithPrefix("\u00A7cPlease select a Map!");
            return;
        }

        if (mapId.contains(" ")) {
            ChatManager.sendMessageWithPrefix("\u00A7cThe Map-ID can't contain spaces!");
            return;
        }

        if (position.replace(" ", "").equals("")) {
            ChatManager.sendMessageWithPrefix("\u00A7cPlease add a valid position!");
            return;
        }

        int x = parseIntSafe(getTextFieldValue("x"));
        int y = parseIntSafe(getTextFieldValue("y"));
        int z = parseIntSafe(getTextFieldValue("z"));

        this.stratReminder = new StratReminder(
                null,
                mapId,
                position,
                facing,
                setup,
                strat,
                comment,
                x,
                y,
                z
        );

        if (isEditMenu) {
            stratReminder.setId(stratMap.getOrDefault("id", null));
        }

        srm.addReminder(stratReminder);

        if (Minecraft.getMinecraft().thePlayer != null) {
            ChatManager.sendMessageWithPrefix("\u00A7aSuccessfully created StratReminder!");
        }
        this.mc.displayGuiScreen(null);
    }

    private void deleteReminder() {
        String position = getTextFieldValue("Position");
        String facing = getTextFieldValue("Facing");
        String setup = getTextFieldValue("Setup");
        String strat = getTextFieldValue("Strat");
        String comment = getTextFieldValue("Comment");
        String mapId = getTextFieldValue("Map ID");

        int x = parseIntSafe(getTextFieldValue("x"));
        int y = parseIntSafe(getTextFieldValue("y"));
        int z = parseIntSafe(getTextFieldValue("z"));

        this.stratReminder = new StratReminder(
                stratMap.getOrDefault("id", null),
                mapId,
                position,
                facing,
                setup,
                strat,
                comment,
                x,
                y,
                z
        );
        srm.removeReminder(stratReminder.getId());
    }

    private int parseIntSafe(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String getTextFieldValue(String fieldId) {
        syncTextFieldsToMap();
        return stratMap.getOrDefault(fieldId.toLowerCase(), "");
    }

    private void syncTextFieldsToMap() {
        for (GuiTextFieldWithStringID field : textFields) {
            String fieldId = field.getStringId().toLowerCase();
            stratMap.put(fieldId, field.getText());
        }
    }

    private void nextReminder(boolean right) {
        syncTextFieldsToMap();
        if (right) {
            currentReminder++;
        } else {
            currentReminder--;
        }

        loadEditMenu();
        initGui();
    }
}
