package com.tangykiwi.kiwiclient.util.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.util.render.state.CustomCircleRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomLineRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomQuadRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomRoundedQuadRenderState;

import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.joml.Math;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;
import org.joml.Options;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.awt.Color;

public class RenderUtils {
    public static int getGuiScale() {
        return (int) mc.getWindow().getGuiScale();
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

    public static void drawRectWH(GuiGraphicsExtractor context, float x, float y, float width, float height, int c) {
        drawRectXY(context, x, y, x + width, y + height, c);
    }

    public static void drawRectXY(GuiGraphicsExtractor context, float x, float y, float x2, float y2, int c) {
        context.guiRenderState.addGuiElement(new CustomQuadRenderState(
            context,
            x, y, x2, y2,
            c
        ));
    }

    public static void drawRoundedQuadWH(GuiGraphicsExtractor context, float x, float y, float width, float height, float rad, float samples, int c) {
        drawRoundedQuadXY(context, x, y, x + width, y + height, rad, samples, c);
    }

    public static void drawRoundedQuadXY(GuiGraphicsExtractor context, float x, float y, float x2, float y2, float rad, float samples, int c) {
        drawRoundedQuadInternal(context, x, y, x2, y2, rad, samples, c);
    }

    private static void drawRoundedQuadInternal(GuiGraphicsExtractor context, float x1, float y1, float x2, float y2, float rad, float samples, int color) {
        context.guiRenderState.addGuiElement(new CustomRoundedQuadRenderState(
            context,
            x1, y1, x2, y2, rad, samples,
            color
        ));
    }

    public static void drawLine2D(GuiGraphicsExtractor context, float x1, float y1, float x2, float y2, float thickness, int c)
    {
        context.guiRenderState.addGuiElement(new CustomLineRenderState(
            context,
            x1, y1, x2, y2,
            thickness,
            c
        ));
    }

    public static void drawCircle(GuiGraphicsExtractor context, float x, float y, float radius, int c)
    {
        context.guiRenderState.addGuiElement(new CustomCircleRenderState(
            context,
            x, y, 0, 360, radius, 90,
            c
        ));
    }

    // /**
    //  * Differs from DrawContext.fillGradient as this allows renderUtils to render
    //  * our intended objects "on top" of the gradient
    //  */
    // public static void fillGradient(int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
    //     BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    //     bufferBuilder.addVertex(startX, startY, 0).setColor(colorStart);
    //     bufferBuilder.addVertex(startX, endY, 0).setColor(colorEnd);
    //     bufferBuilder.addVertex(endX, endY, 0).setColor(colorEnd);
    //     bufferBuilder.addVertex(endX, startY, 0).setColor(colorStart);
    //     RenderLayer.getDebugQuads().draw(bufferBuilder.build());
    // }

    public static void drawItem(GuiGraphicsExtractor drawContext, ItemStack itemStack, int x, int y, float scale) {
        drawItem(drawContext, itemStack, x, y, scale, false, null);
    }

    public static void drawItem(GuiGraphicsExtractor drawContext, ItemStack itemStack, int x, int y, float scale, boolean overlay, String countOverride) {
        Matrix3x2fStack matrices = drawContext.pose();
        matrices.pushMatrix();
        matrices.scale(scale, scale);

        int scaledX = (int) (x / scale);
        int scaledY = (int) (y / scale);

        drawContext.item(itemStack, scaledX, scaledY);
        if (overlay) drawContext.itemDecorations(mc.font, itemStack, scaledX, scaledY, countOverride);

        matrices.popMatrix();
    }

    public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, int color, double lineWidth) {
        PoseStack matrices = matrixFrom(x1, y1, z1);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        Vertexer.vertexLine(matrices, bufferBuilder, 0, 0, 0, (float)(x2 - x1), (float)(y2 - y1), (float)(z2 - z1), color);
        CustomRenderLayers.LINES.apply(lineWidth).draw(bufferBuilder.build());
    }

    public static void drawQuad(double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        PoseStack matrices = matrixFrom(x1, y1, z1);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float dx = (float)(x2 - x1);
        float dy = (float)(y2 - y1);
        float dz = (float)(z2 - z1);
        Vertexer.vertexQuad(matrices, bufferBuilder, 0, 0, 0, 0, 0, dz, dx, dy, dz, 0, dy, 0, 2, color);
        CustomRenderLayers.QUADS.draw(bufferBuilder.build());
    }

    public static void drawWorldText(PoseStack matrices, Quaternionf rotation, float height, String text, int color, VertexConsumer vertexConsumers, Font textRenderer, float scale) {
        matrices.pushPose();
        matrices.translate(0, height + 0.2f, 0);
        matrices.scale(3, 3, 3);
        // matrices.multiply(rotation);

        float max_x = -0.5f;
        float min_x = 0.5f;
        float min_y = 0;
        float max_y = 0.1f;
        float z = -0.0001f;

        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f model = matrices.last().pose();
        bufferBuilder.addVertex(model, min_x, min_y, z).setLight(0xF000F0).setColor(color);
        bufferBuilder.addVertex(model, max_x, min_y, z).setLight(0xF000F0).setColor(color);
        bufferBuilder.addVertex(model, max_x, max_y, z).setLight(0xF000F0).setColor(color);
        bufferBuilder.addVertex(model, min_x, max_y, z).setLight(0xF000F0).setColor(color);
        CustomRenderLayers.QUADS.draw(bufferBuilder.build());

        matrices.pushPose();
        matrices.scale(0.01f, -0.01f, 0.01f);
        textRenderer.drawInBatch(text, -(textRenderer.width(text)) / 2f, -9, 0xFFFFFFFF, false, matrices.last().pose(), KiwiClient.mc.renderBuffers().bufferSource(), Font.DisplayMode.SEE_THROUGH, 0, 0xF000F0);
        matrices.popPose();
        matrices.popPose();
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
        float normalSqrt = Math.sqrt(dx * dx + dy * dy + dz * dz);
        return new Vector3f(dx / normalSqrt, dy / normalSqrt, dz / normalSqrt);
    }

    public static void drawBoxOutline(BlockPos blockPos, int color, double lineWidth, Direction... excludeDirs) {
        drawBoxOutline(new AABB(blockPos), color, lineWidth, excludeDirs);
    }

    public static void drawBoxOutline(AABB box, int color, double lineWidth, Direction... excludeDirs) {
        // if (!mc.worldRenderer.frustum.isVisible(box)) return;

        PoseStack matrices = matrixFrom(box.minX, box.minY, box.minZ);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_COLOR_NORMAL);
        Vertexer.vertexBoxOutline(matrices, bufferBuilder, box.move(new Vec3(box.minX, box.minY, box.minZ).reverse()), color, excludeDirs);
        CustomRenderLayers.LINES.apply(lineWidth).draw(bufferBuilder.build());
    }

    public static void drawBoxFilled(BlockPos blockPos, int color, Direction... excludeDirs) {
        drawBoxFilled(new AABB(blockPos), color, excludeDirs);
    }

    public static void drawBoxFilled(AABB box, int color, Direction... excludeDirs) {
        // if (!mc.worldRenderer.frustum.isVisible(box)) return;

        PoseStack matrices = matrixFrom(box.minX, box.minY, box.minZ);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Vertexer.vertexBoxFilled(matrices, bufferBuilder, box.move(new Vec3(box.minX, box.minY, box.minZ).reverse()), color, excludeDirs);
        CustomRenderLayers.QUADS.draw(bufferBuilder.build());
    }

    public static PoseStack matrixFrom(double x, double y, double z) {
		PoseStack matrices = new PoseStack();

		Camera camera = mc.gameRenderer.getMainCamera();
        matrices.mulPose(Axis.XP.rotationDegrees(camera.xRot()));
        matrices.mulPose(Axis.YP.rotationDegrees(camera.yRot()));

		matrices.translate(x - camera.position().x, y - camera.position().y, z - camera.position().z);

		return matrices;
	}

    public static Vec3 getInterpolationOffset(Entity e) {
		if (mc.isPaused()) {
			return Vec3.ZERO;
		}

		double tickDelta = mc.getDeltaTracker().getGameTimeDeltaPartialTick(true);
		return new Vec3(
				e.getX() - Mth.lerp(tickDelta, e.xo, e.getX()),
				e.getY() - Mth.lerp(tickDelta, e.yo, e.getY()),
				e.getZ() - Mth.lerp(tickDelta, e.zo, e.getZ()));
	}
}
