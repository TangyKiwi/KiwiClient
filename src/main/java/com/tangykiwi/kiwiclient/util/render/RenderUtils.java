package com.tangykiwi.kiwiclient.util.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tangykiwi.kiwiclient.mixin.GuiGraphicsExtractorAccessor;
import com.tangykiwi.kiwiclient.util.render.state.CustomCircleRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomLineRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomQuadRenderState;
import com.tangykiwi.kiwiclient.util.render.state.CustomRoundedQuadRenderState;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix3x2fStack;

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
        GuiGraphicsExtractorAccessor contextAccessor = (GuiGraphicsExtractorAccessor) context;
        contextAccessor.getGuiRenderState().addGuiElement(new CustomQuadRenderState(
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
        GuiGraphicsExtractorAccessor contextAccessor = (GuiGraphicsExtractorAccessor) context;
        contextAccessor.getGuiRenderState().addGuiElement(new CustomRoundedQuadRenderState(
            context,
            x1, y1, x2, y2, rad, samples,
            color
        ));
    }

    public static void drawLine2D(GuiGraphicsExtractor context, float x1, float y1, float x2, float y2, float thickness, int c)
    {
        GuiGraphicsExtractorAccessor contextAccessor = (GuiGraphicsExtractorAccessor) context;
        contextAccessor.getGuiRenderState().addGuiElement(new CustomLineRenderState(
            context,
            x1, y1, x2, y2,
            thickness,
            c
        ));
    }

    public static void drawCircle(GuiGraphicsExtractor context, float x, float y, float radius, int c)
    {
        GuiGraphicsExtractorAccessor contextAccessor = (GuiGraphicsExtractorAccessor) context;
        contextAccessor.getGuiRenderState().addGuiElement(new CustomCircleRenderState(
            context,
            x, y, 0, 360, radius, 90,
            c
        ));
    }

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

    public static void drawLine(SubmitNodeStorage submitNodeStorage, double x1, double y1, double z1, double x2, double y2, double z2, int color, float lineWidth) {
        submitNodeStorage.submitCustomGeometry(
            new PoseStack(), 
            CustomRenderLayers.LINES, 
            (entry, buffer) -> {
                Vertexer.vertexLine(entry, buffer, (float) x1, (float) y1, (float) z1, (float) x2, (float) y2, (float) z2, color, lineWidth);
            }
        );
    }

    public static void drawQuad(SubmitNodeStorage submitNodeStorage, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        float dx = (float)(x2 - x1);
        float dy = (float)(y2 - y1);
        float dz = (float)(z2 - z1);

        submitNodeStorage.submitCustomGeometry(
            new PoseStack(), 
            CustomRenderLayers.QUADS, 
            (entry, buffer) -> {
                Vertexer.vertexQuad(entry, buffer, 0, 0, 0, 0, 0, dz, dx, dy, dz, 0, dy, 0, 2, color);
            }
        );
    }

    public static void drawBoxOutline(SubmitNodeStorage submitNodeStorage, BlockPos blockPos, int color, float lineWidth, Direction... excludeDirs) {
        drawBoxOutline(submitNodeStorage, new AABB(blockPos), color, lineWidth, excludeDirs);
    }

    public static void drawBoxOutline(SubmitNodeStorage submitNodeStorage, AABB box, int color, float lineWidth, Direction... excludeDirs) {
        submitNodeStorage.submitCustomGeometry(
            new PoseStack(), 
            CustomRenderLayers.LINES, 
            (entry, buffer) -> {
                Vertexer.vertexBoxOutline(entry, buffer, box.move(mc.gameRenderer.mainCamera().position().reverse()), color, lineWidth, excludeDirs);
            }
        );
    }

    public static void drawBoxFilled(SubmitNodeStorage submitNodeStorage, BlockPos blockPos, int color, Direction... excludeDirs) {
        drawBoxFilled(submitNodeStorage, new AABB(blockPos), color, excludeDirs);
    }

    public static void drawBoxFilled(SubmitNodeStorage submitNodeStorage, AABB box, int color, Direction... excludeDirs) {
        submitNodeStorage.submitCustomGeometry(
            new PoseStack(), 
            CustomRenderLayers.QUADS, 
            (entry, buffer) -> {
                Vertexer.vertexBoxFilled(entry, buffer, box.move(mc.gameRenderer.mainCamera().position().reverse()), color, excludeDirs);
            }
        );
    }
}
