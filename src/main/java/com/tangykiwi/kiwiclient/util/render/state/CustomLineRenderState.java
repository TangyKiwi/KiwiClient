package com.tangykiwi.kiwiclient.util.render.state;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector2d;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;

public record CustomLineRenderState(RenderPipeline pipeline,
    TextureSetup textureSetup, Matrix3x2f pose, float x, float y, float x2,
    float y2, float thickness, int color, @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
    
    public CustomLineRenderState(Matrix3x2f pose, float x, float y, float x2,
        float y2, float thickness, int color, @Nullable ScreenRectangle scissorArea) {
        this(RenderPipelines.GUI, TextureSetup.noTexture(), pose, x, y, x2, y2,
            thickness, color, scissorArea, createBounds(x, y, x2, y2, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices) {
        var offset = new Vector2d(x2() - x(), y2() - y()).perpendicular().normalize().mul(thickness() * .5d);

        vertices.addVertexWith2DPose(pose, (float) (x() + offset.x), (float) (y() + offset.y)).setColor(color());
        vertices.addVertexWith2DPose(pose, (float) (x() - offset.x), (float) (y() - offset.y)).setColor(color());
        vertices.addVertexWith2DPose(pose, (float) (x2() - offset.x), (float) (y2() - offset.y)).setColor(color());
        vertices.addVertexWith2DPose(pose, (float) (x2() + offset.x), (float) (y2() + offset.y)).setColor(color());
    }

    @Nullable
    private static ScreenRectangle createBounds(float x, float y, float x2, float y2,
        Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle screenRect = new ScreenRectangle(
            (int) Math.min(x, x2), (int) Math.min(y, y2),
            (int) Math.abs(x2 - x), (int) Math.abs(y2 - y)
        ).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect)
            : screenRect;
    }
}
