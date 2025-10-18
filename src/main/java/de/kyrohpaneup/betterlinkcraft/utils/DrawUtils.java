package de.kyrohpaneup.betterlinkcraft.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.List;

public class DrawUtils {

    public static void drawNametag(String str) {
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
        float scale = 1.6F;
        float scaledSize = 0.016666668F * scale;
        GlStateManager.pushMatrix();
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.scale(-scaledSize, -scaledSize, scaledSize);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int yOffset = 0;
        int halfWidth = fontrenderer.getStringWidth(str) / 2;
        GlStateManager.disableTexture2D();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(-halfWidth - 1, -1 + yOffset, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(-halfWidth - 1, 8 + yOffset, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(halfWidth + 1, 8 + yOffset, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos(halfWidth + 1, -1 + yOffset, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, yOffset, 553648127);
        GlStateManager.depthMask(true);
        fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, yOffset, -1);
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void drawNametagAtCoords(String str, int blockX, float blockY, int blockZ, float partialTicks) {
        GlStateManager.alphaFunc(516, 0.1F);
        EntityPlayerSP viewer = Minecraft.getMinecraft().thePlayer;
        double viewerX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * (double)partialTicks;
        double viewerY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * (double)partialTicks;
        double viewerZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * (double)partialTicks;
        double x = (double)blockX - viewerX;
        double y = (double)blockY - viewerY - (double)viewer.getEyeHeight();
        double z = (double)blockZ - viewerZ;

        x = blockX >= 0 ? x + 0.5D : x - 0.5D;
        z = blockZ >= 0 ? z + 0.5D : z - 0.5D;

        double distSq = x * x + y * y + z * z;
        double dist = Math.sqrt(distSq);
        if (!(distSq > 144.0D)) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.translate(0.0F, viewer.getEyeHeight(), 0.0F);
            drawNametag(str);
            GlStateManager.popMatrix();
        }
    }

    public static void drawTextAtCoords(List<String> str, BlockPos loc, float partialTicks) {
        float lineSpacing = 0.24F;
        float y = (float)loc.getY() + 2;

        for (String s : str) {
            if (s.length() > 0) {
                drawNametagAtCoords(s, loc.getX(), y -= lineSpacing, loc.getZ(), partialTicks);
            }
        }
    }
}
