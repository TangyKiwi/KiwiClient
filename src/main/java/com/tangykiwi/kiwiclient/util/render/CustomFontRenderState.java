package com.tangykiwi.kiwiclient.util.render;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record CustomFontRenderState(RenderPipeline pipeline,
    TextureSetup textureSetup, Matrix3x2f pose, float xo, float yo, float w, float h, float mult, float u1, float u2, float v1, float v2, float cr, float cg, float cb, float ca,
    @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {
    
    public CustomFontRenderState(Matrix3x2f pose, GpuTextureView glId, float xo, float yo, float w, float h, float mult, float u1, float u2, float v1, float v2, float cr, float cg, float cb, float ca,
        @Nullable ScreenRect scissorArea) {
        this(RenderPipelines.GUI_TEXTURED, TextureSetup.of(glId), pose, xo, yo, w, h, mult, u1, u2, v1, v2,
            cr, cg, cb, ca,
            scissorArea,
            createBounds(xo, yo, w, h, mult, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        vertices.vertex(pose(), xo + 0, yo + h * mult, depth).texture(u1, v2).color(cr, cg, cb, ca);
        vertices.vertex(pose(), xo + w, yo + h * mult, depth).texture(u2, v2).color(cr, cg, cb, ca);
        vertices.vertex(pose(), xo + w, yo + 0, depth).texture(u2, v1).color(cr, cg, cb, ca);
        vertices.vertex(pose(), xo + 0, yo + 0, depth).texture(u1, v1).color(cr, cg, cb, ca);
    }

    @Nullable
    private static ScreenRect createBounds(float xo, float yo, float w, float h, float mult, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        float minX = xo;
        float maxX = xo + w;
        float minY = yo;
        float maxY = yo + h * mult;

        ScreenRect screenRect = new ScreenRect((int) minX, (int) minY,
            (int) (maxX - minX), (int) (maxY - minY)).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect)
            : screenRect;
    }
}
