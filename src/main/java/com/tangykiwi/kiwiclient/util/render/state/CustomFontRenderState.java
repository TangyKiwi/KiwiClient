package com.tangykiwi.kiwiclient.util.render.state;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;

public record CustomFontRenderState(RenderPipeline pipeline,
    TextureSetup textureSetup, Matrix3x2f pose, float xo, float yo, float w, float h, float mult, float u1, float u2, float v1, float v2, float cr, float cg, float cb, float ca,
    @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
    
    public CustomFontRenderState(Matrix3x2f pose, GpuTextureView glId, float xo, float yo, float w, float h, float mult, float u1, float u2, float v1, float v2, float cr, float cg, float cb, float ca,
        @Nullable ScreenRectangle scissorArea) {
        this(RenderPipelines.GUI_TEXTURED, TextureSetup.singleTexture(glId, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST)), pose, xo, yo, w, h, mult, u1, u2, v1, v2,
            cr, cg, cb, ca,
            scissorArea,
            createBounds(xo, yo, w, h, mult, pose, scissorArea));
    }

    public CustomFontRenderState(GuiGraphicsExtractor context, GpuTextureView glId, float xo, float yo, float w, float h, float mult, float u1, float u2, float v1, float v2, float cr, float cg, float cb, float ca) {
        this(RenderPipelines.GUI_TEXTURED, TextureSetup.singleTexture(glId, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST)), context.pose(), xo, yo, w, h, mult, u1, u2, v1, v2,
            cr, cg, cb, ca,
            context.scissorStack.peek(),
            createBounds(xo, yo, w, h, mult, context.pose(), context.scissorStack.peek()));
    }

    @Override
    public void buildVertices(VertexConsumer vertices) {
        vertices.addVertexWith2DPose(pose(), xo + 0, yo + h * mult).setUv(u1, v2).setColor(cr, cg, cb, ca);
        vertices.addVertexWith2DPose(pose(), xo + w, yo + h * mult).setUv(u2, v2).setColor(cr, cg, cb, ca);
        vertices.addVertexWith2DPose(pose(), xo + w, yo + 0).setUv(u2, v1).setColor(cr, cg, cb, ca);
        vertices.addVertexWith2DPose(pose(), xo + 0, yo + 0).setUv(u1, v1).setColor(cr, cg, cb, ca);
    }

    @Nullable
    private static ScreenRectangle createBounds(float xo, float yo, float w, float h, float mult, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        float minX = xo;
        float maxX = xo + w;
        float minY = yo;
        float maxY = yo + h * mult;

        ScreenRectangle screenRect = new ScreenRectangle((int) minX, (int) minY,
            (int) (maxX - minX), (int) (maxY - minY)).transformMaxBounds(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect)
            : screenRect;
    }
}
