package de.kyrohpaneup.betterlinkcraft.gui.impl.speedrun;

import de.kyrohpaneup.betterlinkcraft.BetterLinkCraft;
import de.kyrohpaneup.betterlinkcraft.managers.ChatManager;
import de.kyrohpaneup.betterlinkcraft.managers.TimerManager;
import de.kyrohpaneup.betterlinkcraft.settings.Option;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class SpeedrunCompleteMenu extends GuiScreen {

    private final GuiScreen parentScreen;
    private final TimerManager timerManager;
    private final List<SegmentResult> segmentResults;
    private int tableTop;
    private int tableBottom;
    private int column1X, column2X, column3X;
    private final int columnWidth = 100;
    private float scrollAmount = 0.0F;
    private int maxScroll = 0;
    private boolean isScrolling = false;

    public SpeedrunCompleteMenu(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.timerManager = BetterLinkCraft.INSTANCE.getTimerManager();
        this.segmentResults = generateResults();
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 30, 200, 20, "Copy to Clipboard"));

        this.tableTop = 60;
        this.tableBottom = this.height - 60;
        int tableCenterX = this.width / 2;
        this.column1X = tableCenterX - columnWidth / 2 - columnWidth;
        this.column2X = tableCenterX - columnWidth / 2;
        this.column3X = tableCenterX + columnWidth / 2;

        calculateMaxScroll();
    }

    private void calculateMaxScroll() {
        int contentHeight = segmentResults.size() * 15;
        int visibleHeight = tableBottom - tableTop - 10;
        this.maxScroll = Math.max(0, contentHeight - visibleHeight);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(this.fontRendererObj, "Speedrun Results", this.width / 2, 15, 0xFFFFFF);
        drawCenteredString(this.fontRendererObj, Option.SELECTED_SPEEDRUN_MAP.getStringValue(), this.width / 2, 30, 0xAAAAAA);

        drawString(this.fontRendererObj, "Segment", column1X, tableTop - 15, 0xFFFFFF);
        drawString(this.fontRendererObj, "IGT", column2X, tableTop - 15, 0xFFFFFF);
        drawString(this.fontRendererObj, "RTA", column3X, tableTop - 15, 0xFFFFFF);

        drawHorizontalLine(column1X - 5, column3X + columnWidth - 5, tableTop - 5, 0xFFFFFFFF);


        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = new net.minecraft.client.gui.ScaledResolution(mc).getScaleFactor();
        GL11.glScissor(column1X * scaleFactor, (this.height - tableBottom) * scaleFactor,
                (column3X + columnWidth - column1X) * scaleFactor, (tableBottom - tableTop) * scaleFactor);

        int yPos = tableTop + 5 - (int)scrollAmount;
        for (SegmentResult result : segmentResults) {
            if (yPos + 15 > tableTop && yPos < tableBottom) {
                drawString(this.fontRendererObj, result.segmentName, column1X, yPos, 0xFFFFFF);

                drawString(this.fontRendererObj, result.igtTime, column2X, yPos, 0xFFFF55);

                drawString(this.fontRendererObj, result.rtaTime, column3X, yPos, 0x55FFFF);
            }
            yPos += 15;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (maxScroll > 0) {
            drawScrollBar();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawScrollBar() {
        int scrollBarLeft = column3X + columnWidth + 5;
        int scrollBarRight = scrollBarLeft + 6;
        int scrollBarTop = tableTop;
        int scrollBarBottom = tableBottom;

        drawRect(scrollBarLeft, scrollBarTop, scrollBarRight, scrollBarBottom, 0xFF666666);

        int thumbHeight = Math.max(20, (int)((float)(scrollBarBottom - scrollBarTop) *
                ((float)(scrollBarBottom - scrollBarTop) / (float)(segmentResults.size() * 15 + 20))));
        int thumbTop = scrollBarTop + (int)((float)(scrollBarBottom - scrollBarTop - thumbHeight) *
                (scrollAmount / (float)maxScroll));

        drawRect(scrollBarLeft, thumbTop, scrollBarRight, thumbTop + thumbHeight, 0xFFCCCCCC);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            takeScreenshot();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

        int scroll = Mouse.getEventDWheel();
        if (scroll != 0 && maxScroll > 0) {
            scrollAmount -= (float)scroll * 0.5F;
            scrollAmount = MathHelper.clamp_float(scrollAmount, 0.0F, (float)maxScroll);
        }

        // Drag-Scrolling
        if (Mouse.isButtonDown(0)) {
            if (!isScrolling && mouseX >= column1X && mouseX <= column3X + columnWidth &&
                    mouseY >= tableTop && mouseY <= tableBottom) {
                isScrolling = true;
            }
        } else {
            isScrolling = false;
        }
    }

    private void takeScreenshot() {
        try {
            Framebuffer framebuffer = this.mc.getFramebuffer();
            int width = framebuffer.framebufferWidth;
            int height = framebuffer.framebufferHeight;

            IntBuffer pixelBuffer = BufferUtils.createIntBuffer(width * height);
            int[] pixelValues = new int[width * height];

            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            pixelBuffer.clear();

            if (OpenGlHelper.isFramebufferEnabled()) {
                GlStateManager.bindTexture(framebuffer.framebufferTexture);
                GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
            } else {
                GL11.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
            }

            pixelBuffer.get(pixelValues);

            BufferedImage bufferedimage = new BufferedImage(width, height, 1);
            bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
            BufferedImage correctedImage = new BufferedImage(width, height, 1);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    correctedImage.setRGB(x, height - 1 - y, bufferedimage.getRGB(x, y));
                }
            }

            copyImageToClipboard(correctedImage);

            if (this.mc.thePlayer != null) {
                ChatManager.sendMessageWithPrefix("\u00A7aCopied results to clipboard!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (this.mc.thePlayer != null) {
                ChatManager.sendMessageWithPrefix("\u00A7cFailed to copy the results.");
            }
        }
    }

    private void copyImageToClipboard(BufferedImage image) {
        try {
            if (!GraphicsEnvironment.isHeadless()) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                TransferableImage transferable = new TransferableImage(image);
                clipboard.setContents(transferable, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class TransferableImage implements Transferable {
        private final BufferedImage image;

        public TransferableImage(BufferedImage image) {
            this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) {
            if (isDataFlavorSupported(flavor)) {
                return image;
            }
            return null;
        }
    }

    private static class SegmentResult {
        public String segmentName;
        public String igtTime;
        public String rtaTime;

        public SegmentResult(String segmentName, String igtTime, String rtaTime) {
            this.segmentName = segmentName;
            this.igtTime = igtTime;
            this.rtaTime = rtaTime;
        }
    }

    private List<SegmentResult> generateResults() {
        List<SegmentResult> results = new ArrayList<>();
        List<String> segments = new ArrayList<>(timerManager.getSegments());
        segments.add("End");
        for (String segment : segments) {
            results.add(new SegmentResult(segment,
                    timerManager.formatTime(timerManager.igtSegmentMap.getOrDefault(segment, 0L)),
                    timerManager.formatTime(timerManager.rtaSegmentMap.getOrDefault(segment, 0L))));
        }
        return results;
    }
}