package com.tangykiwi.kiwiclient.util.render.state;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tangykiwi.kiwiclient.util.render.CustomRenderPipelines;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;

public record CustomCircleRenderState(RenderPipeline pipeline,
    TextureSetup textureSetup, Matrix3x2f pose, float x, float y, float a1,
    float a2, float rad, int samples, int color, @Nullable ScreenRectangle scissorArea,
    @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
        
    public CustomCircleRenderState(Matrix3x2f pose, float x, float y, float a1,
        float a2, float rad, int samples, int color, @Nullable ScreenRectangle scissorArea) {
        this(CustomRenderPipelines.GUI_TRIANGLE_FAN, TextureSetup.noTexture(), pose, x, y, a1, a2, rad, samples, color,
        scissorArea, createBounds(x, y, rad, pose, scissorArea));
    }

    @Override
    public void buildVertices(VertexConsumer vertices) {
        double angleStep = Math.toRadians(a2() - a1()) / samples();


        for (int i = samples(); i >= 0; i--) {
            double theta = Math.toRadians(a1()) + i * angleStep;
            vertices.addVertexWith2DPose(pose(), x(), y()).setColor(color());
            vertices.addVertexWith2DPose(pose(), (float) (x() - Math.cos(theta) * rad()), (float) (y() - Math.sin(theta) * rad()))
                .setColor(color());
            vertices.addVertexWith2DPose(pose(), (float) (x() - Math.cos(theta + angleStep) * rad()), (float) (y() - Math.sin(theta + angleStep) * rad()))
                .setColor(color());
        }
    }
    
    @Nullable
    private static ScreenRectangle createBounds(float x, float y, float rad,
        Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle screenRect = new ScreenRectangle(
            (int) (x - rad), (int) (y - rad),
            (int) (rad * 2), (int) (rad * 2)
        ).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect)
            : screenRect;
    }
    
}
