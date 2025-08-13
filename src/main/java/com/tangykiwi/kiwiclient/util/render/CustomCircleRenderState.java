package com.tangykiwi.kiwiclient.util.render;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record CustomCircleRenderState(RenderPipeline pipeline,
    TextureSetup textureSetup, Matrix3x2f pose, float x, float y, float a1,
    float a2, float rad, int samples, int color, @Nullable ScreenRect scissorArea,
    @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {
        
    public CustomCircleRenderState(Matrix3x2f pose, float x, float y, float a1,
        float a2, float rad, int samples, int color, @Nullable ScreenRect scissorArea) {
        this(CustomRenderPipelines.GUI_TRIANGLE_FAN, TextureSetup.empty(), pose, x, y, a1, a2, rad, samples, color,
        scissorArea, createBounds(x, y, rad, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        double angleStep = Math.toRadians(a2() - a1()) / samples();


        for (int i = samples(); i >= 0; i--) {
            double theta = Math.toRadians(a1()) + i * angleStep;
            vertices.vertex(pose(), x(), y(), depth).color(color());
            vertices.vertex(pose(), (float) (x() - Math.cos(theta) * rad()), (float) (y() - Math.sin(theta) * rad()), depth)
                .color(color());
            vertices.vertex(pose(), (float) (x() - Math.cos(theta + angleStep) * rad()), (float) (y() - Math.sin(theta + angleStep) * rad()), depth)
                .color(color());
        }
    }
    
    @Nullable
    private static ScreenRect createBounds(float x, float y, float rad,
        Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        ScreenRect screenRect = new ScreenRect(
            new ScreenPos((int) (x - rad), (int) (y - rad)),
            (int) (rad * 2), (int) (rad * 2)
        ).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect)
            : screenRect;
    }
    
}
