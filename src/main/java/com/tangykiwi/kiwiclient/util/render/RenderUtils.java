package com.tangykiwi.kiwiclient.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.tangykiwi.kiwiclient.util.render.state.CustomCircleRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomLineRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomQuadRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomRoundedQuadRenderState;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

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

    public static void drawBoxOutline(MatrixStack matrixStack, BlockPos blockPos, int color, float lineWidth, Direction... excludeDirs) {
        drawBoxOutline(matrixStack, new Box(blockPos), color, lineWidth, excludeDirs);
    }

    public static void drawBoxOutline(MatrixStack matrixStack, Box box, int color, float lineWidth, Direction... excludeDirs) {
        Matrix4f matrices = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        RenderSystem.lineWidth(lineWidth);

        // bottom
        bufferBuilder.vertex(matrices, x1, y1, z1).color(color);
        bufferBuilder.vertex(matrices, x2, y1, z1).color(color);
        bufferBuilder.vertex(matrices, x2, y1, z1).color(color);
        bufferBuilder.vertex(matrices, x2, y1, z2).color(color);
        bufferBuilder.vertex(matrices, x2, y1, z2).color(color);
        bufferBuilder.vertex(matrices, x1, y1, z2).color(color);
        bufferBuilder.vertex(matrices, x1, y1, z2).color(color);
        bufferBuilder.vertex(matrices, x1, y1, z1).color(color);

        // top
        bufferBuilder.vertex(matrices, x1, y2, z1).color(color);
        bufferBuilder.vertex(matrices, x2, y2, z1).color(color);
        bufferBuilder.vertex(matrices, x2, y2, z1).color(color);
        bufferBuilder.vertex(matrices, x2, y2, z2).color(color);
        bufferBuilder.vertex(matrices, x2, y2, z2).color(color);
        bufferBuilder.vertex(matrices, x1, y2, z2).color(color);
        bufferBuilder.vertex(matrices, x1, y2, z2).color(color);
        bufferBuilder.vertex(matrices, x1, y2, z1).color(color);

        // side
        bufferBuilder.vertex(matrices, x1, y1, z1).color(color);
        bufferBuilder.vertex(matrices, x1, y2, z1).color(color);
        bufferBuilder.vertex(matrices, x2, y1, z1).color(color);
        bufferBuilder.vertex(matrices, x2, y2, z1).color(color);
        bufferBuilder.vertex(matrices, x2, y1, z2).color(color);
        bufferBuilder.vertex(matrices, x2, y2, z2).color(color);
        bufferBuilder.vertex(matrices, x1, y1, z2).color(color);
        bufferBuilder.vertex(matrices, x1, y2, z2).color(color);

        CustomRenderLayers.LINES.draw(bufferBuilder.end());
    }

    public static MatrixStack matrixFrom(double x, double y, double z) {
		MatrixStack matrices = new MatrixStack();

		Camera camera = mc.gameRenderer.getCamera();
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

		matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

		return matrices;
	}
}
