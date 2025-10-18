package de.kyrohpaneup.betterlinkcraft.gui.impl.sheet;

import de.kyrohpaneup.betterlinkcraft.mods.sheet.GoogleSheetsAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiSheetViewer extends GuiScreen {

    public final GoogleSheetsAPI.SheetData sheetData;
    private int scrollOffset = 0;
    private int baseRowHeight = 20;
    private int columnWidth = 100;
    private int headerHeight = 30;
    private int visibleRows;
    private int visibleColumns;
    private int startX, startY;
    private int tableWidth, tableHeight;
    private boolean isDraggingScrollbar = false;
    private int scrollbarWidth = 8;
    private int scrollbarHeight;
    private int scrollbarX, scrollbarY;
    private int maxScrollOffset;
    private int horizontalScrollOffset = 0;
    private int maxHorizontalScrollOffset = 0;
    private boolean isDraggingHorizontalScrollbar = false;
    private int horizontalScrollbarHeight = 8;
    private int horizontalScrollbarY;

    private List<Integer> rowHeights = new ArrayList<>();
    private List<Integer> rowPositions = new ArrayList<>();
    private int totalContentHeight = 0;

    public GuiSheetViewer(GoogleSheetsAPI.SheetData sheetData) {
        this.fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
        this.sheetData = sheetData;
        calculateRowHeights();
    }

    /**
     * Berechnet die Höhe für jede Zeile basierend auf dem Inhalt
     */
    private void calculateRowHeights() {
        rowHeights.clear();
        rowPositions.clear();
        totalContentHeight = 0;

        if (sheetData == null || sheetData.data == null) return;

        for (int row = 0; row < sheetData.data.length; row++) {
            int maxLines = 1;

            try {
                for (int col = 0; col < sheetData.data[row].length; col++) {
                    String cellText = sheetData.data[row][col] != null ? sheetData.data[row][col] : "";
                    if (!cellText.isEmpty()) {
                        List<String> lines = wrapText(cellText, columnWidth - 8);
                        if (lines != null && !lines.isEmpty()) {
                            maxLines = Math.max(maxLines, lines.size());
                        }
                    }
                }
            } catch (Exception e) {
                maxLines = 1;
            }

            maxLines = Math.min(maxLines, 10);

            int rowHeight = baseRowHeight + (maxLines - 1) * (fontRendererObj.FONT_HEIGHT + 2);
            rowHeight = Math.max(baseRowHeight, rowHeight);

            rowHeights.add(rowHeight);
            rowPositions.add(totalContentHeight);
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

    /**
     * Gibt die Zeilenhöhe für eine bestimmte Zeile zurück
     */
    private int getRowHeight(int row) {
        if (row < 0 || row >= rowHeights.size()) return baseRowHeight;
        return rowHeights.get(row);
    }

    /**
     * Gibt die Y-Position einer Zeile im Gesamtinhalt zurück
     */
    private int getRowPosition(int row) {
        if (row < 0 || row >= rowPositions.size()) return 0;
        return rowPositions.get(row);
    }

    /**
     * Findet die Zeile basierend auf der Scroll-Position
     */
    private int findRowAtScrollPosition(int scrollPixels) {
        if (rowPositions.isEmpty()) return 0;

        for (int i = 0; i < rowPositions.size() - 1; i++) {
            if (scrollPixels >= rowPositions.get(i) && scrollPixels < rowPositions.get(i + 1)) {
                return i;
            }
        }

        return rowPositions.size() - 1;
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

        visibleColumns = tableWidth / columnWidth;

        scrollbarX = startX + tableWidth - scrollbarWidth;
        scrollbarY = startY + headerHeight;
        scrollbarHeight = tableHeight - headerHeight - horizontalScrollbarHeight;

        horizontalScrollbarY = startY + tableHeight - horizontalScrollbarHeight;

        maxScrollOffset = Math.max(0, totalContentHeight - (tableHeight - headerHeight - horizontalScrollbarHeight));
        if (sheetData != null && sheetData.data.length > 0) {
            int totalColumnsWidth = sheetData.data[0].length * columnWidth;
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

        drawHeader(mouseX, mouseY);

        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        drawData(mouseX, mouseY);
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();

        drawScrollbar(mouseX, mouseY);

        if (maxHorizontalScrollOffset > 0) {
            drawHorizontalScrollbar(mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawHeader(int mouseX, int mouseY) {
        drawRect(startX, startY, startX + tableWidth, startY + headerHeight, 0x80666666);

        drawRect(startX, startY + headerHeight - 1, startX + tableWidth, startY + headerHeight, 0xFFCCCCCC);

        drawRect(startX, startY, startX + 30, startY + headerHeight, 0x80555555);
        drawRect(startX + 29, startY, startX + 30, startY + headerHeight, 0xFF666666);
        drawCenteredString(fontRendererObj, "Row", startX + 15, startY + (headerHeight - 8) / 2, 0xFFFFFF);

        int x = startX + 30 - horizontalScrollOffset;
        if (sheetData.data.length > 0) {
            int totalColumns = sheetData.data[0].length;

            for (int col = 0; col < totalColumns; col++) {
                int columnStartX = x;
                int columnEndX = x + columnWidth;

                boolean isVisible = columnEndX > startX + 30 && columnStartX < startX + tableWidth - scrollbarWidth;

                if (isVisible) {
                    String headerText = "Column " + (col + 1);
                    if (col < sheetData.data[0].length && sheetData.data[0][col] != null && !sheetData.data[0][col].isEmpty()) {
                        headerText = sheetData.data[0][col];
                    }

                    int visibleStartX = Math.max(columnStartX, startX + 30);
                    int visibleEndX = Math.min(columnEndX, startX + tableWidth - scrollbarWidth);

                    drawRect(visibleStartX, startY, visibleEndX, startY + headerHeight, 0x80444444);

                    if (visibleStartX > startX + 30) {
                        drawRect(visibleStartX - 1, startY, visibleStartX, startY + headerHeight, 0xFF666666);
                    }

                    if (visibleEndX < startX + tableWidth - scrollbarWidth && visibleEndX == columnEndX) {
                        drawRect(visibleEndX - 1, startY, visibleEndX, startY + headerHeight, 0xFF666666);
                    }

                    if (visibleEndX - columnStartX > 8) {
                        String displayText = headerText;
                        int availableWidth = columnEndX - columnStartX - 8;

                        if (fontRendererObj.getStringWidth(displayText) > availableWidth) {
                            displayText = fontRendererObj.trimStringToWidth(displayText, availableWidth - 3) + "...";
                        }

                        int textX = columnStartX + 4;
                        int textEndX = textX + fontRendererObj.getStringWidth(displayText);

                        if (textEndX > startX + 30 && textX < startX + tableWidth - scrollbarWidth) {
                            int visibleTextStartX = Math.max(textX, startX + 30);
                            int visibleTextEndX = Math.min(textEndX, startX + tableWidth - scrollbarWidth);

                            if (textX < startX + 30) {
                                int hiddenLeftWidth = startX + 30 - textX;
                                String visibleText = getVisibleTextFromRight(displayText, visibleTextEndX - visibleTextStartX);
                                if (!visibleText.isEmpty()) {
                                    drawString(fontRendererObj, visibleText, visibleTextStartX, startY + (headerHeight - 8) / 2, 0xFFFFFF);
                                }
                            }
                            else if (textEndX > startX + tableWidth - scrollbarWidth) {
                                int maxVisibleWidth = (startX + tableWidth - scrollbarWidth) - textX - 2;
                                if (maxVisibleWidth > 3) {
                                    displayText = fontRendererObj.trimStringToWidth(displayText, maxVisibleWidth - 3) + "...";
                                    drawString(fontRendererObj, displayText, textX, startY + (headerHeight - 8) / 2, 0xFFFFFF);
                                }
                            }
                            else {
                                drawString(fontRendererObj, displayText, textX, startY + (headerHeight - 8) / 2, 0xFFFFFF);
                            }
                        }
                    }
                }

                x += columnWidth;
            }
        }

        drawRect(startX + 30 - 1, startY, startX + 30, startY + headerHeight, 0xFF666666);
    }

    private String getVisibleTextFromRight(String text, int maxWidth) {
        if (maxWidth <= 3) return "...";

        for (int i = text.length(); i > 0; i--) {
            String testText = text.substring(text.length() - i);
            if (fontRendererObj.getStringWidth(testText) <= maxWidth) {
                return testText;
            }
        }

        return "...";
    }

    private void drawData(int mouseX, int mouseY) {
        if (sheetData.data.length <= 1) return;

        int startRow = 1;
        int currentY = startY + headerHeight - scrollOffset;
        int tableTop = startY + headerHeight;
        int tableBottom = startY + tableHeight - horizontalScrollbarHeight;

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
                if (numberY >= tableTop && numberY + fontRendererObj.FONT_HEIGHT <= tableBottom) {
                    drawString(fontRendererObj, String.valueOf(row), startX + 2, numberY, 0xCCCCCC);
                }

                int x = startX + 30 - horizontalScrollOffset;
                int totalColumns = sheetData.data[row].length;

                for (int col = 0; col < totalColumns; col++) {
                    int columnStartX = x;
                    int columnEndX = x + columnWidth;

                    boolean isVisible = columnEndX > startX + 30 && columnStartX < startX + tableWidth - scrollbarWidth;

                    if (isVisible) {
                        String cellText = sheetData.data[row][col] != null ? sheetData.data[row][col] : "";

                        int visibleCellStartX = Math.max(columnStartX, startX + 30);
                        int visibleCellEndX = Math.min(columnEndX, startX + tableWidth - scrollbarWidth);

                        drawRect(visibleCellStartX, visibleStartY, visibleCellEndX, visibleEndY, rowColor);

                        if (!cellText.isEmpty()) {
                            int availableCellWidth = columnWidth - 8;
                            List<String> lines = wrapText(cellText, availableCellWidth);

                            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                                int lineY = currentY + 6 + (lineIndex * (fontRendererObj.FONT_HEIGHT + 2));

                                if (lineY >= tableTop && lineY + fontRendererObj.FONT_HEIGHT <= tableBottom) {
                                    String displayText = lines.get(lineIndex);

                                    int textX = columnStartX + 4;
                                    int textEndX = textX + fontRendererObj.getStringWidth(displayText);

                                    if (textEndX > startX + 30 && textX < startX + tableWidth - scrollbarWidth) {
                                        int visibleTextStartX = Math.max(textX, startX + 30);
                                        int visibleTextEndX = Math.min(textEndX, startX + tableWidth - scrollbarWidth);
                                        int visibleWidth = visibleTextEndX - visibleTextStartX;

                                        if (textX < startX + 30) {
                                            if (visibleWidth > 3) {
                                                String visibleText = getVisibleTextFromRight(displayText, visibleWidth);
                                                drawString(fontRendererObj, visibleText, visibleTextStartX, lineY, 0xFFFFFF);
                                            }
                                        }
                                        else if (textEndX > startX + tableWidth - scrollbarWidth) {
                                            if (visibleWidth > 3) {
                                                displayText = fontRendererObj.trimStringToWidth(displayText, visibleWidth - 3) + "...";
                                                drawString(fontRendererObj, displayText, textX, lineY, 0xFFFFFF);
                                            }
                                        }
                                        else {
                                            drawString(fontRendererObj, displayText, textX, lineY, 0xFFFFFF);
                                        }
                                    }
                                }
                            }
                        }

                        if (visibleCellEndX < startX + tableWidth - scrollbarWidth) {
                            drawRect(visibleCellEndX - 1, visibleStartY, visibleCellEndX, visibleEndY, 0x40666666);
                        }

                        if (visibleCellStartX > startX + 30) {
                            drawRect(visibleCellStartX - 1, visibleStartY, visibleCellStartX, visibleEndY, 0x40666666);
                        }
                    }

                    x += columnWidth;
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
        int totalWidth = sheetData.data[0].length * columnWidth;
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
            int visibleHeight = tableHeight - headerHeight;
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

        if (maxHorizontalScrollOffset > 0) {
            int visibleWidth = tableWidth - scrollbarWidth - 30;
            int totalWidth = sheetData.data[0].length * columnWidth;
            int thumbWidth = Math.max(20, visibleWidth * visibleWidth / totalWidth);
            int thumbX = startX + 30 + (horizontalScrollOffset * (visibleWidth - thumbWidth) / maxHorizontalScrollOffset);

            if (mouseX >= thumbX && mouseX <= thumbX + thumbWidth &&
                    mouseY >= horizontalScrollbarY && mouseY <= horizontalScrollbarY + horizontalScrollbarHeight) {
                isDraggingHorizontalScrollbar = true;
            }
        }

        if (maxScrollOffset > 0) {
            int visibleHeight = tableHeight - headerHeight;
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
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (isDraggingHorizontalScrollbar && maxHorizontalScrollOffset > 0) {
            int visibleWidth = tableWidth - scrollbarWidth - 30;
            int thumbWidth = Math.max(20, (int)(visibleWidth * (float)visibleWidth / (sheetData.data[0].length * columnWidth)));
            int relativeX = mouseX - startX - thumbWidth / 2;
            float scrollPercent = (float) relativeX / (visibleWidth - thumbWidth);
            horizontalScrollOffset = (int) (scrollPercent * maxHorizontalScrollOffset);
            horizontalScrollOffset = Math.max(0, Math.min(maxHorizontalScrollOffset, horizontalScrollOffset));
        }

        if (isDraggingScrollbar && maxScrollOffset > 0) {
            int visibleHeight = tableHeight - headerHeight;
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
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
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
            scrollOffset = Math.max(0, scrollOffset - (tableHeight - headerHeight) / 2);
        } else if (keyCode == 209) { // PAGE DOWN
            scrollOffset = Math.min(maxScrollOffset, scrollOffset + (tableHeight - headerHeight) / 2);
        }

        super.keyTyped(typedChar, keyCode);
    }
}