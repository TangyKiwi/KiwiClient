package com.tangykiwi.kiwiclient.util.render.state;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Vector2d;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record CustomLineRenderState(RenderPipeline pipeline,
    TextureSetup textureSetup, Matrix3x2f pose, float x, float y, float x2,
    float y2, float thickness, int color, @Nullable ScreenRect scissorArea,
    @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {
    
    public CustomLineRenderState(Matrix3x2f pose, float x, float y, float x2,
        float y2, float thickness, int color, @Nullable ScreenRect scissorArea) {
        this(RenderPipelines.GUI, TextureSetup.empty(), pose, x, y, x2, y2,
            thickness, color, scissorArea, createBounds(x, y, x2, y2, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        var offset = new Vector2d(x2() - x(), y2() - y()).perpendicular().normalize().mul(thickness() * .5d);

        vertices.vertex(pose, (float) (x() + offset.x), (float) (y() + offset.y), depth).color(color());
        vertices.vertex(pose, (float) (x() - offset.x), (float) (y() - offset.y), depth).color(color());
        vertices.vertex(pose, (float) (x2() - offset.x), (float) (y2() - offset.y), depth).color(color());
        vertices.vertex(pose, (float) (x2() + offset.x), (float) (y2() + offset.y), depth).color(color());
    }

    @Nullable
    private static ScreenRect createBounds(float x, float y, float x2, float y2,
        Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        ScreenRect screenRect = new ScreenRect(
            (int) Math.min(x, x2), (int) Math.min(y, y2),
            (int) Math.abs(x2 - x), (int) Math.abs(y2 - y)
        ).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect)
            : screenRect;
    }
}
