package com.tangykiwi.kiwiclient.util.render;

import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.*;
import net.minecraft.item.ItemStack;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

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

    public static void drawRectWH(DrawContext context, float x, float y, float width, float height, int c) {
        drawRectXY(context, x, y, x + width, y + height, c);
    }

    public static void drawRectXY(DrawContext context, float x, float y, float x2, float y2, int c) {
        Matrix3x2f matrix = new Matrix3x2f(context.getMatrices());
        ScreenRect scissor = context.scissorStack.peekLast();
        context.state.addSimpleElement(new CustomQuadRenderState(
            matrix,
            x, y, x2, y2,
            c,
            scissor
        ));
    }

    public static void drawRoundedQuadWH(DrawContext context, float x, float y, float width, float height, float rad, float samples, int c) {
        drawRoundedQuadXY(context, x, y, x + width, y + height, rad, samples, c);
    }

    public static void drawRoundedQuadXY(DrawContext context, float x, float y, float x2, float y2, float rad, float samples, int c) {
        drawRoundedQuadInternal(context, x, y, x2, y2, rad, samples, c);
    }

    private static void drawRoundedQuadInternal(DrawContext context, float x1, float y1, float x2, float y2, float rad, float samples, int color) {
        Matrix3x2f matrix = new Matrix3x2f(context.getMatrices());
        ScreenRect scissor = context.scissorStack.peekLast();
        context.state.addSimpleElement(new CustomRoundedQuadRenderState(
            matrix,
            x1, y1, x2, y2, rad, samples,
            color,
            scissor
        ));
    }

    public static void drawLine2D(DrawContext context, float x1, float y1, float x2, float y2, float thickness, int c)
    {
        Matrix3x2f matrix = new Matrix3x2f(context.getMatrices());
        ScreenRect scissor = context.scissorStack.peekLast();
        context.state.addSimpleElement(new CustomLineRenderState(
            matrix,
            x1, y1, x2, y2,
            thickness,
            c,
            scissor
        ));
    }

    public static void drawCircle(DrawContext context, float x, float y, float radius, int c)
    {
        Matrix3x2f matrix = new Matrix3x2f(context.getMatrices());
        ScreenRect scissor = context.scissorStack.peekLast();
        context.state.addSimpleElement(new CustomCircleRenderState(
            matrix,
            x, y, 0, 360, radius, 90,
            c,
            scissor
        ));
    }

    /**
     * Differs from DrawContext.fillGradient as this allows renderUtils to render
     * our intended objects "on top" of the gradient
     */
    public static void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(startX, startY, 0).color(colorStart);
        bufferBuilder.vertex(startX, endY, 0).color(colorEnd);
        bufferBuilder.vertex(endX, endY, 0).color(colorEnd);
        bufferBuilder.vertex(endX, startY, 0).color(colorStart);
        RenderLayer.getDebugQuads().draw(bufferBuilder.end());
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
}
