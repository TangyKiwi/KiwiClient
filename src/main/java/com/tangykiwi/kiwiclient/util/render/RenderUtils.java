package com.tangykiwi.kiwiclient.util.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.tangykiwi.kiwiclient.util.render.state.CustomCircleRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomLineRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomQuadRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomRoundedQuadRenderState;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Options;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, double lineWidth) {
        MatrixStack matrices = matrixFrom(x1, y1, z1);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL);
        Vertexer.vertexLine(matrices, bufferBuilder, 0, 0, 0, (float)(x2 - x1), (float)(y2 - y1), (float)(z2 - z1), color);
        CustomRenderLayers.LINES.apply(lineWidth).draw(bufferBuilder.end());
    }

    public static void drawQuad(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        MatrixStack matrices = matrixFrom(x1, y1, z1);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float dx = (float)(x2 - x1);
        float dy = (float)(y2 - y1);
        float dz = (float)(z2 - z1);
        Vertexer.vertexQuad(matrices, bufferBuilder, 0, 0, 0, 0, 0, dz, dx, dy, dz, 0, dy, 0, 2, color);
        CustomRenderLayers.QUADS.draw(bufferBuilder.end());
    }

    public static void drawWorldText(MatrixStack matrices, Quaternionf rotation, float height, String text, int color, VertexConsumerProvider vertexConsumers, TextRenderer textRenderer, float scale) {
        matrices.push();
        matrices.translate(0, height + 0.2f, 0);
        matrices.scale(3, 3, 3);
        // matrices.multiply(rotation);

        float max_x = -0.5f;
        float min_x = 0.5f;
        float min_y = 0;
        float max_y = 0.1f;
        float z = -0.0001f;

        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f model = matrices.peek().getPositionMatrix();
        bufferBuilder.vertex(model, min_x, min_y, z).light(0xF000F0).color(color);
        bufferBuilder.vertex(model, max_x, min_y, z).light(0xF000F0).color(color);
        bufferBuilder.vertex(model, max_x, max_y, z).light(0xF000F0).color(color);
        bufferBuilder.vertex(model, min_x, max_y, z).light(0xF000F0).color(color);
        CustomRenderLayers.QUADS.draw(bufferBuilder.end());

        matrices.push();
        matrices.scale(0.01f, -0.01f, 0.01f);
        textRenderer.draw(text, -(textRenderer.getWidth(text)) / 2f, -9, 0xFFFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextLayerType.SEE_THROUGH, 0, 0xF000F0);
        matrices.pop();
        matrices.pop();
    }

    // public static void drawLine(MatrixStack matrixStack, float x1, float y1, float z1, float x2, float y2, float z2, int color, double lineWidth) {
    //     Matrix4f matrices = matrixStack.peek().getPositionMatrix();
    //     BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL);

    //     Vector3f normal = getNormal(x1, y1, z1, x2, y2, z2);
    //     bufferBuilder.vertex(matrices, x1, y1, z1).color(color).normal(matrixStack.peek(), normal.x(), normal.y(), normal.z());
    //     bufferBuilder.vertex(matrices, x2, y2, z2).color(color).normal(matrixStack.peek(), normal.x(), normal.y(), normal.z());

    //     CustomRenderLayers.LINES.apply(lineWidth).draw(bufferBuilder.end());
    // }

    // public static void drawQuad(MatrixStack matrixStack, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color) {
    //     Matrix4f matrices = matrixStack.peek().getPositionMatrix();
    //     BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

    //     bufferBuilder.vertex(matrices, x1,  y1,  z1).color(color);
    //     bufferBuilder.vertex(matrices, x2,  y2,  z2).color(color);
    //     bufferBuilder.vertex(matrices, x3,  y3,  z3).color(color);
    //     bufferBuilder.vertex(matrices, x4,  y4,  z4).color(color);

    //     CustomRenderLayers.QUADS.draw(bufferBuilder.end());
    // }

    public static Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float normalSqrt = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
        return new Vector3f(dx / normalSqrt, dy / normalSqrt, dz / normalSqrt);
    }

    public static void drawBoxOutline(BlockPos blockPos, int color, double lineWidth, Direction... excludeDirs) {
        drawBoxOutline(new Box(blockPos), color, lineWidth, excludeDirs);
    }

    public static void drawBoxOutline(Box box, int color, double lineWidth, Direction... excludeDirs) {
        // if (!mc.worldRenderer.frustum.isVisible(box)) return;

        MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL);
        Vertexer.vertexBoxOutline(matrices, bufferBuilder, box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate()), color, excludeDirs);
        CustomRenderLayers.LINES.apply(lineWidth).draw(bufferBuilder.end());
    }

    public static void drawBoxFilled(BlockPos blockPos, int color, Direction... excludeDirs) {
        drawBoxFilled(new Box(blockPos), color, excludeDirs);
    }

    public static void drawBoxFilled(Box box, int color, Direction... excludeDirs) {
        // if (!mc.worldRenderer.frustum.isVisible(box)) return;

        MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Vertexer.vertexBoxFilled(matrices, bufferBuilder, box.offset(new Vec3d(box.minX, box.minY, box.minZ).negate()), color, excludeDirs);
        CustomRenderLayers.QUADS.draw(bufferBuilder.end());
    }

    public static MatrixStack matrixFrom(double x, double y, double z) {
		MatrixStack matrices = new MatrixStack();

		Camera camera = mc.gameRenderer.getCamera();
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

		matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

		return matrices;
	}

    public static Vec3d getInterpolationOffset(Entity e) {
		if (mc.isPaused()) {
			return Vec3d.ZERO;
		}

		double tickDelta = mc.getRenderTickCounter().getTickProgress(true);
		return new Vec3d(
				e.getX() - MathHelper.lerp(tickDelta, e.lastRenderX, e.getX()),
				e.getY() - MathHelper.lerp(tickDelta, e.lastRenderY, e.getY()),
				e.getZ() - MathHelper.lerp(tickDelta, e.lastRenderZ, e.getZ()));
	}
}
