package de.kyrohpaneup.betterlinkcraft.gui.impl.sheet;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.gui.impl.strat.CreateStratReminderGui;
import de.kyrohpaneup.betterlinkcraft.mods.sheet.GoogleSheetsAPI;
import de.kyrohpaneup.betterlinkcraft.mods.stratreminders.StratReminder;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiSheetViewer extends GuiScreen {

    public final GoogleSheetsAPI.SheetData sheetData;

    private double scaleFactor = 1.0;

    private final int columnWidth = 100;

    private int scaledBaseRowHeight;
    private int scaledColumnWidth;
    private int scaledHeaderHeight;

    private int scrollOffset = 0;
    private int startX, startY;
    private int tableWidth, tableHeight;
    private boolean isDraggingScrollbar = false;
    private final int scrollbarWidth = 8;
    private int scrollbarHeight;
    private int scrollbarX, scrollbarY;
    private int maxScrollOffset;
    private int horizontalScrollOffset = 0;
    private int maxHorizontalScrollOffset = 0;
    private boolean isDraggingHorizontalScrollbar = false;
    private final int horizontalScrollbarHeight = 8;
    private int horizontalScrollbarY;

    private final List<Integer> rowHeights = new ArrayList<>();
    private int totalContentHeight = 0;

    public GuiSheetViewer(GoogleSheetsAPI.SheetData sheetData) {
        this.fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
        this.sheetData = sheetData;
        updateScaledDimensions();
        calculateRowHeights();
    }


    public void setScale(double newScale) {
        this.scaleFactor = Math.max(0.25, Math.min(4.0, newScale));
        updateScaledDimensions();
        calculateRowHeights();
        initGui();
    }

    private void updateScaledDimensions() {
        int baseRowHeight = 20;
        this.scaledBaseRowHeight = (int) (baseRowHeight * scaleFactor);
        this.scaledColumnWidth = (int) (this.columnWidth * scaleFactor);
        int headerHeight = 30;
        this.scaledHeaderHeight = (int) (headerHeight * scaleFactor);
    }

    private void calculateRowHeights() {
        rowHeights.clear();
        totalContentHeight = 0;

        if (sheetData == null || sheetData.data == null) return;

        for (int row = 0; row < sheetData.data.length; row++) {
            int maxLines = 1;

            try {
                for (int col = 0; col < sheetData.data[row].length; col++) {
                    String cellText = sheetData.data[row][col] != null ? sheetData.data[row][col] : "";
                    if (!cellText.isEmpty()) {
                        List<String> lines = wrapText(cellText, (int)((columnWidth - 8) * scaleFactor));
                        if (lines != null && !lines.isEmpty()) {
                            maxLines = Math.max(maxLines, lines.size());
                        }
                    }
                }
            } catch (Exception e) {
                maxLines = 1;
            }

            maxLines = Math.min(maxLines, 10);

            int scaledFontHeight = (int) (fontRendererObj.FONT_HEIGHT * scaleFactor);
            int scaledPadding = (int) (2 * scaleFactor);

            int rowHeight = scaledBaseRowHeight + (maxLines - 1) * (scaledFontHeight + scaledPadding);
            rowHeight = Math.max(scaledBaseRowHeight, rowHeight);

            rowHeights.add(rowHeight);
            totalContentHeight += rowHeight;
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            lines.add("");
            return lines;
        }

        try {
            return fontRendererObj.listFormattedStringToWidth(text, maxWidth);
        } catch (Exception e) {
            String safeText = text.replace("\n", " ").replace("\r", " ");
            int chunkSize = Math.max(5, maxWidth / 6);

            for (int i = 0; i < safeText.length(); i += chunkSize) {
                int end = Math.min(safeText.length(), i + chunkSize);
                String line = safeText.substring(i, end);
                if (i + chunkSize < safeText.length() && end > i + 5) {
                    line = line.trim() + "...";
                }
                lines.add(line);
                if (lines.size() >= 5) break;
            }
        }

        return lines;
    }

    private int getRowHeight(int row) {
        if (row < 0 || row >= rowHeights.size()) return scaledBaseRowHeight;
        return rowHeights.get(row);
    }

    @Override
    public void initGui() {
        super.initGui();

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        startX = 50;
        startY = 50;
        tableWidth = screenWidth - 100;
        tableHeight = screenHeight - 100;

        scrollbarX = startX + tableWidth - scrollbarWidth;
        scrollbarY = startY + scaledHeaderHeight;
        scrollbarHeight = tableHeight - scaledHeaderHeight - horizontalScrollbarHeight;

        horizontalScrollbarY = startY + tableHeight - horizontalScrollbarHeight;

        maxScrollOffset = Math.max(0, totalContentHeight - (tableHeight - scaledHeaderHeight - horizontalScrollbarHeight));
        if (sheetData != null && sheetData.data.length > 0) {
            int totalColumnsWidth = sheetData.data[0].length * scaledColumnWidth;
            maxHorizontalScrollOffset = Math.max(0, totalColumnsWidth - (tableWidth - scrollbarWidth - 30));
        }

        scrollOffset = Math.min(scrollOffset, maxScrollOffset);
        horizontalScrollOffset = Math.min(horizontalScrollOffset, maxHorizontalScrollOffset);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        String title = "Sheet Viewer";
        if (sheetData != null && sheetData.sheet != null) {
            title = sheetData.sheet.name + " (" + sheetData.data.length + " rows)";
        }
        drawCenteredString(fontRendererObj, title, width / 2, 20, 0xFFFFFF);

        if (sheetData == null || sheetData.data == null || sheetData.data.length == 0) {
            drawCenteredString(fontRendererObj, "No data available", width / 2, height / 2, 0xFF6666);
            return;
        }

        drawRect(startX, startY, startX + tableWidth, startY + tableHeight, 0x80000000);

        drawHeader();

        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        drawData();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();

        drawScrollbar(mouseX, mouseY);

        if (maxHorizontalScrollOffset > 0) {
            drawHorizontalScrollbar(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawHeader() {
        drawRect(startX, startY, startX + tableWidth, startY + scaledHeaderHeight, 0x80666666);
        drawRect(startX, startY + scaledHeaderHeight - 1, startX + tableWidth, startY + scaledHeaderHeight, 0xFFCCCCCC);

        drawRect(startX, startY, startX + 30, startY + scaledHeaderHeight, 0x80555555);
        drawRect(startX + 29, startY, startX + 30, startY + scaledHeaderHeight, 0xFF666666);

        drawCenteredString(fontRendererObj, "Row", startX + 15, startY + (scaledHeaderHeight - 8) / 2, 0xFFFFFF);

        ScaledResolution sr = new ScaledResolution(this.mc);
        int scale = sr.getScaleFactor();

        int scissorX = (startX + 30) * scale;
        int scissorWidth = (tableWidth - scrollbarWidth - 30) * scale;
        int scissorHeight = scaledHeaderHeight * scale;

        int scissorY = this.mc.displayHeight - ((startY + scaledHeaderHeight) * scale);

        int x = startX + 30 - horizontalScrollOffset;
        if (sheetData.data.length > 0) {
            int totalColumns = sheetData.data[0].length;

            for (int col = 0; col < totalColumns; col++) {
                int columnStartX = x;
                int columnEndX = x + scaledColumnWidth;

                boolean isVisible = columnEndX > startX + 30 && columnStartX < startX + tableWidth - scrollbarWidth;

                if (isVisible) {
                    String headerText = "Column " + (col + 1);
                    if (col < sheetData.data[0].length && sheetData.data[0][col] != null && !sheetData.data[0][col].isEmpty()) {
                        headerText = sheetData.data[0][col];
                    }

                    int visibleStartX = Math.max(columnStartX, startX + 30);
                    int visibleEndX = Math.min(columnEndX, startX + tableWidth - scrollbarWidth);

                    drawRect(visibleStartX, startY, visibleEndX, startY + scaledHeaderHeight, 0x80444444);

                    if (visibleStartX > startX + 30) {
                        drawRect(visibleStartX - 1, startY, visibleStartX, startY + scaledHeaderHeight, 0xFF666666);
                    }

                    if (visibleEndX < startX + tableWidth - scrollbarWidth && visibleEndX == columnEndX) {
                        drawRect(visibleEndX - 1, startY, visibleEndX, startY + scaledHeaderHeight, 0xFF666666);
                    }

                    if (visibleEndX - columnStartX > 8) {
                        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
                        org.lwjgl.opengl.GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);

                        GlStateManager.pushMatrix();
                        GlStateManager.translate(columnStartX + 4, startY + (scaledHeaderHeight - (8 * scaleFactor)) / 2, 0);
                        GlStateManager.scale(scaleFactor, scaleFactor, 1.0);

                        String displayText = headerText;
                        int unscaledAvailableWidth = (int)((columnEndX - columnStartX - 8) / scaleFactor);

                        if (fontRendererObj.getStringWidth(displayText) > unscaledAvailableWidth) {
                            displayText = fontRendererObj.trimStringToWidth(displayText, unscaledAvailableWidth - 3) + "...";
                        }

                        fontRendererObj.drawString(displayText, 0, 0, 0xFFFFFF);
                        GlStateManager.popMatrix();

                        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
                    }
                }
                x += scaledColumnWidth;
            }
        }
        drawRect(startX + 30 - 1, startY, startX + 30, startY + scaledHeaderHeight, 0xFF666666);
    }

    private void drawData() {
        if (sheetData.data.length <= 1) return;

        int startRow = 1;
        int currentY = startY + scaledHeaderHeight - scrollOffset;
        int tableTop = startY + scaledHeaderHeight;
        int tableBottom = startY + tableHeight - horizontalScrollbarHeight;

        ScaledResolution sr = new ScaledResolution(this.mc);
        int scale = sr.getScaleFactor();

        int scissorX = (startX + 30) * scale;
        int scissorWidth = (tableWidth - scrollbarWidth - 30) * scale;
        int scissorHeight = (tableHeight - scaledHeaderHeight - horizontalScrollbarHeight) * scale;

        int scissorY = this.mc.displayHeight - (tableBottom * scale);

        for (int row = startRow; row < sheetData.data.length; row++) {
            int rowHeight = getRowHeight(row);

            if (currentY + rowHeight <= tableTop) {
                currentY += rowHeight;
                continue;
            }
            if (currentY >= tableBottom) {
                break;
            }

            int visibleStartY = Math.max(currentY, tableTop);
            int visibleEndY = Math.min(currentY + rowHeight, tableBottom);

            if (visibleStartY < visibleEndY) {
                int rowColor = (row % 2 == 0) ? 0x80222222 : 0x80333333;
                drawRect(startX, visibleStartY, startX + tableWidth - scrollbarWidth, visibleEndY, rowColor);

                int numberY = currentY + 6;
                if (numberY >= tableTop && numberY + (fontRendererObj.FONT_HEIGHT * scaleFactor) <= tableBottom) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(startX + 2, numberY, 0);
                    GlStateManager.scale(scaleFactor, scaleFactor, 1.0);
                    fontRendererObj.drawString(String.valueOf(row), 0, 0, 0xCCCCCC);
                    GlStateManager.popMatrix();
                }

                int x = startX + 30 - horizontalScrollOffset;
                int totalColumns = sheetData.data[row].length;

                for (int col = 0; col < totalColumns; col++) {
                    int columnStartX = x;
                    int columnEndX = x + scaledColumnWidth;

                    boolean isVisible = columnEndX > startX + 30 && columnStartX < startX + tableWidth - scrollbarWidth;

                    if (isVisible) {
                        String cellText = sheetData.data[row][col] != null ? sheetData.data[row][col] : "";

                        int visibleCellStartX = Math.max(columnStartX, startX + 30);
                        int visibleCellEndX = Math.min(columnEndX, startX + tableWidth - scrollbarWidth);

                        drawRect(visibleCellStartX, visibleStartY, visibleCellEndX, visibleEndY, rowColor);

                        if (!cellText.isEmpty()) {
                            int unscaledAvailableWidth = (int)((scaledColumnWidth - 8) / scaleFactor);
                            List<String> lines = wrapText(cellText, unscaledAvailableWidth);

                            org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
                            org.lwjgl.opengl.GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);

                            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                                int lineY = currentY + 6 + (int)(lineIndex * ((fontRendererObj.FONT_HEIGHT + 2) * scaleFactor));

                                if (lineY >= tableTop && lineY + (fontRendererObj.FONT_HEIGHT * scaleFactor) <= tableBottom) {
                                    String displayText = lines.get(lineIndex);

                                    GlStateManager.pushMatrix();
                                    GlStateManager.translate(columnStartX + 4, lineY, 0);
                                    GlStateManager.scale(scaleFactor, scaleFactor, 1.0);

                                    fontRendererObj.drawString(displayText, 0, 0, 0xFFFFFF);
                                    GlStateManager.popMatrix();
                                }
                            }

                            org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_SCISSOR_TEST);
                        }

                        if (visibleCellEndX < startX + tableWidth - scrollbarWidth) {
                            drawRect(visibleCellEndX - 1, visibleStartY, visibleCellEndX, visibleEndY, 0x40666666);
                        }
                        if (visibleCellStartX > startX + 30) {
                            drawRect(visibleCellStartX - 1, visibleStartY, visibleCellStartX, visibleEndY, 0x40666666);
                        }
                    }
                    x += scaledColumnWidth;
                }
                drawRect(startX, visibleEndY - 1, startX + tableWidth - scrollbarWidth, visibleEndY, 0x40666666);
            }
            currentY += rowHeight;
        }

        drawRect(startX, tableTop - 1, startX + tableWidth - scrollbarWidth, tableTop, 0xFFCCCCCC);
        drawRect(startX + 30 - 1, tableTop, startX + 30, tableBottom, 0xFF666666);
    }

    private void drawHorizontalScrollbar(int mouseX, int mouseY) {
        if (maxHorizontalScrollOffset <= 0) return;

        drawRect(startX + 30, horizontalScrollbarY, startX + tableWidth - scrollbarWidth, horizontalScrollbarY + horizontalScrollbarHeight, 0x80444444);

        int visibleWidth = tableWidth - scrollbarWidth - 30;
        int totalWidth = sheetData.data[0].length * scaledColumnWidth;
        int thumbWidth = Math.max(20, visibleWidth * visibleWidth / totalWidth);
        int thumbX = startX + 30 + (horizontalScrollOffset * (visibleWidth - thumbWidth) / maxHorizontalScrollOffset);

        boolean hovered = mouseX >= thumbX && mouseX <= thumbX + thumbWidth &&
                mouseY >= horizontalScrollbarY && mouseY <= horizontalScrollbarY + horizontalScrollbarHeight;
        int thumbColor = hovered || isDraggingHorizontalScrollbar ? 0xFF6666FF : 0xFF4444AA;

        drawRect(thumbX, horizontalScrollbarY, thumbX + thumbWidth, horizontalScrollbarY + horizontalScrollbarHeight, thumbColor);
    }

    private void drawScrollbar(int mouseX, int mouseY) {
        drawRect(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, 0x80444444);

        if (maxScrollOffset > 0) {
            int visibleHeight = tableHeight - scaledHeaderHeight;
            int thumbHeight = Math.max(20, scrollbarHeight * visibleHeight / totalContentHeight);
            int thumbY = scrollbarY + (scrollOffset * (scrollbarHeight - thumbHeight) / maxScrollOffset);

            boolean hovered = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= thumbY && mouseY <= thumbY + thumbHeight;
            int thumbColor = hovered || isDraggingScrollbar ? 0xFF6666FF : 0xFF4444AA;

            drawRect(scrollbarX, thumbY, scrollbarX + scrollbarWidth, thumbY + thumbHeight, thumbColor);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        int tableTop = startY + scaledHeaderHeight;
        int tableBottom = startY + tableHeight - horizontalScrollbarHeight;
        int tableLeft = startX;
        int tableRight = startX + tableWidth - scrollbarWidth;

        if (mouseX >= tableLeft && mouseX <= tableRight && mouseY >= tableTop && mouseY <= tableBottom) {

            int currentY = tableTop - scrollOffset;
            int startRow = 1;

            for (int row = startRow; row < sheetData.data.length; row++) {
                int rowHeight = getRowHeight(row);

                if (mouseY >= currentY && mouseY < currentY + rowHeight) {

                    List<String> rowData = Arrays.asList(sheetData.data[row]);

                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                        handleStratReminder(rowData);
                    }

                    break;
                }

                currentY += rowHeight;
            }
        }

        if (maxHorizontalScrollOffset > 0) {
            int visibleWidth = tableWidth - scrollbarWidth - 30;
            int totalWidth = sheetData.data[0].length * scaledColumnWidth;
            int thumbWidth = Math.max(20, visibleWidth * visibleWidth / totalWidth);
            int thumbX = startX + 30 + (horizontalScrollOffset * (visibleWidth - thumbWidth) / maxHorizontalScrollOffset);

            if (mouseX >= thumbX && mouseX <= thumbX + thumbWidth &&
                    mouseY >= horizontalScrollbarY && mouseY <= horizontalScrollbarY + horizontalScrollbarHeight) {
                isDraggingHorizontalScrollbar = true;
            }
        }

        if (maxScrollOffset > 0) {
            int visibleHeight = tableHeight - scaledHeaderHeight;
            int thumbHeight = Math.max(20, scrollbarHeight * visibleHeight / totalContentHeight);
            int thumbY = scrollbarY + (scrollOffset * (scrollbarHeight - thumbHeight) / maxScrollOffset);

            if (mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                isDraggingScrollbar = true;
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        isDraggingScrollbar = false;
        isDraggingHorizontalScrollbar = false;
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (isDraggingHorizontalScrollbar && maxHorizontalScrollOffset > 0) {
            int visibleWidth = tableWidth - scrollbarWidth - 30;
            int thumbWidth = Math.max(20, (int)(visibleWidth * (float)visibleWidth / (sheetData.data[0].length * scaledColumnWidth)));
            int relativeX = mouseX - startX - thumbWidth / 2;
            float scrollPercent = (float) relativeX / (visibleWidth - thumbWidth);
            horizontalScrollOffset = (int) (scrollPercent * maxHorizontalScrollOffset);
            horizontalScrollOffset = Math.max(0, Math.min(maxHorizontalScrollOffset, horizontalScrollOffset));
        }

        if (isDraggingScrollbar && maxScrollOffset > 0) {
            int visibleHeight = tableHeight - scaledHeaderHeight;
            int thumbHeight = Math.max(20, scrollbarHeight * visibleHeight / totalContentHeight);
            int relativeY = mouseY - scrollbarY - thumbHeight / 2;
            float scrollPercent = (float) relativeY / (scrollbarHeight - thumbHeight);
            scrollOffset = (int) (scrollPercent * maxScrollOffset);
            scrollOffset = Math.max(0, Math.min(maxScrollOffset, scrollOffset));
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int mouseWheelDelta = Mouse.getEventDWheel();
        if (mouseWheelDelta != 0) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                if (mouseWheelDelta > 0) {
                    setScale(scaleFactor + 0.1);
                } else {
                    setScale(scaleFactor - 0.1);
                }
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                if (mouseWheelDelta > 0) {
                    horizontalScrollOffset = Math.max(0, horizontalScrollOffset - 30);
                } else {
                    horizontalScrollOffset = Math.min(maxHorizontalScrollOffset, horizontalScrollOffset + 30);
                }
            } else {
                if (mouseWheelDelta > 0) {
                    scrollOffset = Math.max(0, scrollOffset - 30);
                } else {
                    scrollOffset = Math.min(maxScrollOffset, scrollOffset + 30);
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(null);
        }

        if (keyCode == 200) { // UP
            scrollOffset = Math.max(0, scrollOffset - 20);
        } else if (keyCode == 208) { // DOWN
            scrollOffset = Math.min(maxScrollOffset, scrollOffset + 20);
        } else if (keyCode == 201) { // PAGE UP
            scrollOffset = Math.max(0, scrollOffset - (tableHeight - scaledHeaderHeight) / 2);
        } else if (keyCode == 209) { // PAGE DOWN
            scrollOffset = Math.min(maxScrollOffset, scrollOffset + (tableHeight - scaledHeaderHeight) / 2);
        }

        super.keyTyped(typedChar, keyCode);
    }

    public void handleStratReminder(List<String> data) {

        String map = Option.SELECTED_SR_MAP.getStringValue();
        int positionInt = Option.REMINDER_POSITION.getIntValue();
        int facingInt = Option.REMINDER_FACING.getIntValue();
        int setupInt = Option.REMINDER_SETUP.getIntValue();
        int stratInt = Option.REMINDER_STRAT.getIntValue();
        int strafeInt = Option.REMINDER_STRAFE.getIntValue();
        int commentInt = Option.REMINDER_COMMENT.getIntValue();

        String position = (positionInt > 0 && data.size() >= positionInt) ? data.get(positionInt - 1) : "";
        String facing = (facingInt > 0 && data.size() >= facingInt) ? data.get(facingInt - 1) : "";
        String setup = (setupInt > 0 && data.size() >= setupInt) ? data.get(setupInt - 1) : "";
        String comment = (commentInt > 0 && data.size() >= commentInt) ? data.get(commentInt - 1) : "";

        StringBuilder stratBuilder = new StringBuilder();

        if (stratInt > 0 && data.size() >= stratInt) {
            stratBuilder.append(data.get(stratInt - 1));
        }

        if (strafeInt > 0 && data.size() >= strafeInt) {
            if (stratInt > 0 && data.size() >= stratInt) {
                stratBuilder.append(" ");
            }
            stratBuilder.append(data.get(strafeInt - 1));
        }

        StratReminder reminder = new StratReminder(null, map, position, facing, setup, stratBuilder.toString(), comment,
                (int) mc.thePlayer.posX,
                (int) mc.thePlayer.posY,
                (int) mc.thePlayer.posZ);

        this.mc.displayGuiScreen(new CreateStratReminderGui(BetterLinkCraft.INSTANCE.getSrm().toSimpleMap(reminder), false));
    }
}