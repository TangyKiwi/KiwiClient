package com.tangykiwi.kiwiclient.util;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class RenderUtils {
    public static int getGuiScale() {
        return (int) mc.getWindow().getScaleFactor();
    }

    public static int getRainbowInt(float seconds, float saturation, float brightness) {
        float hue = (System.currentTimeMillis() % (int) (seconds * 1000)) / (float) (seconds * 1000);
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static Color getRainbowColor(float seconds, float saturation, float brightness) {
        return new Color(getRainbowInt(seconds, saturation, brightness));
    }

    public static int getRainbow(float seconds, float saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int) (seconds * 1000)) / (float) (seconds * 1000);
        return Color.HSBtoRGB(hue, saturation, brightness);
    }

    public static void drawRectWH(Matrix3x2fStack matrices, float x, float y, float width, float height, int c) {
        drawRectXY(matrices, x, y, x + width, y + height, c);
    }

    public static void drawRectXY(Matrix3x2fStack matrices, float x, float y, float x2, float y2, int c) {
        setupRender();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrices, x, y2, 0).color(c);
        bufferBuilder.vertex(matrices, x2, y2, 0).color(c);
        bufferBuilder.vertex(matrices, x2, y, 0).color(c);
        bufferBuilder.vertex(matrices, x, y, 0).color(c);
        RenderLayer.getLines().draw(bufferBuilder.end());
        endRender();
    }

    public static void drawRoundedQuadWH(Matrix3x2fStack stack, Color c, float x, float y, float width, float height, float rad, float samples) {
        drawRoundedQuadXY(stack, c, x, y, x + width, y + height, rad, samples);
    }

    public static void drawRoundedQuadXY(Matrix3x2fStack matrices, Color c, float x, float y, float x2, float y2, float rad, float samples) {
        drawRoundedQuad(matrices, c, x, y, x2, y2, rad, rad, rad, rad, samples);
    }

    public static void drawRoundedQuad(Matrix3x2fStack matrices, Color c, float fromX, float fromY, float toX, float toY, float radC1, float radC2, float radC3, float radC4, float samples) {
        setupRender();
        int color = c.getRGB();
        float f = ((float) (color >> 24 & 255) / 255.0F);
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;
        drawRoundedQuadInternal(matrices, g, h, k, f, fromX, fromY, toX, toY, radC1, radC2, radC3, radC4, samples);
        endRender();
    }

    private static void drawRoundedQuadInternal(Matrix3x2fStack matrices, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radC1, double radC2, double radC3, double radC4, double samples) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

        double[][] map = new double[][] { new double[] { toX - radC4, toY - radC4, radC4 }, new double[] { toX - radC2, fromY + radC2, radC2 },
                new double[] { fromX + radC1, fromY + radC1, radC1 }, new double[] { fromX + radC3, toY - radC3, radC3 } };
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                bufferBuilder.vertex(matrices, (float) current[0] + sin, (float) current[1] + cos, 0).color(cr, cg, cb, ca);
            }
            float rad1 = (float) Math.toRadians((360 / 4d + i * 90d));
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            bufferBuilder.vertex(matrices, (float) current[0] + sin, (float) current[1] + cos, 0).color(cr, cg, cb, ca);
        }
        RenderLayer.getLines().draw(bufferBuilder.end());
    }

    public static void drawLine2D(Matrix3x2fStack matrices, float x1, float y1, float x2, float y2, int c)
    {
        setupRender();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES,
                VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrices, x1, y1, 0).color(c);
        bufferBuilder.vertex(matrices, x2, y2, 0).color(c);
        RenderLayer.getLines().draw(bufferBuilder.end());

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        endRender();
    }

    public static void drawCircle(Matrix3x2fStack matrices, float x, float y, float radius, int c)
    {
        setupRender();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        double roundedInterval = (360.0f / 30.0f);

        for (int i = 0; i < 30; i++)
        {
            double angle = Math.toRadians(0 + (i * roundedInterval));
            double angle2 = Math.toRadians(0 + ((i + 1) * roundedInterval));
            float radiusX1 = (float) (Math.cos(angle) * radius);
            float radiusY1 = (float) Math.sin(angle) * radius;
            float radiusX2 = (float) Math.cos(angle2) * radius;
            float radiusY2 = (float) Math.sin(angle2) * radius;

            bufferBuilder.vertex(matrices, x, y, 0).color(c);
            bufferBuilder.vertex(matrices, x + radiusX1, y + radiusY1, 0).color(c);
            bufferBuilder.vertex(matrices, x + radiusX2, y + radiusY2, 0).color(c);
        }
        RenderLayer.getLines().draw(bufferBuilder.end());
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        endRender();
    }

    /**
     * Differs from DrawContext.fillGradient as this allows renderUtils to render
     * our intended objects "on top" of the gradient
     */
    public static void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        setupRender();

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(startX, startY, 0).color(colorStart);
        bufferBuilder.vertex(startX, endY, 0).color(colorEnd);
        bufferBuilder.vertex(endX, endY, 0).color(colorEnd);
        bufferBuilder.vertex(endX, startY, 0).color(colorStart);
        RenderLayer.getLines().draw(bufferBuilder.end());

        endRender();
    }

    public static void drawItem(DrawContext drawContext, ItemStack itemStack, int x, int y, float scale) {
        drawItem(drawContext, itemStack, x, y, scale, false, null);
    }

    public static void drawItem(DrawContext drawContext, ItemStack itemStack, int x, int y, float scale, boolean overlay, String countOverride) {
        Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.scale(scale, scale);

        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        drawContext.drawItem(itemStack, scaledX, scaledY);
        if (overlay) drawContext.drawStackOverlay(mc.textRenderer, itemStack, scaledX, scaledY, countOverride);

        matrices.popMatrix();
    }

    public static void setupRender() {
        // GL11.glEnable(GL11.GL_BLEND);
        
        // RenderSystem.enableBlend();
        // RenderSystem.defaultBlendFunc();
        // RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public static void endRender() {
        // RenderSystem.defaultBlendFunc();
        // RenderSystem.disableBlend();
        // RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    // public static Matrix4f matrixFrom(double x, double y, double z) {
    //     Matrix4f matrices = new Matrix4f();

    //     Camera camera = mc.gameRenderer.getCamera();
    //     matrices.mul
    //     matrices.mul()
    //     matrices.rotate(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()).get);
    //     matrices.rotate(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F).angle());

    //     matrices.translate
    //     matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

    //     return matrices;
    // }
}
