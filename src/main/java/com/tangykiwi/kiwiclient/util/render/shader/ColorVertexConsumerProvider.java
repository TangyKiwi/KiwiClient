package com.tangykiwi.kiwiclient.util.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.*;
import net.minecraft.client.render.RenderPhase.TextureBase;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ColorVertexConsumerProvider {

    private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate(new BufferAllocator(256));

    private Supplier<ShaderProgram> shader;
    private Function<TextureBase, RenderLayer> layerCreator;

    public ColorVertexConsumerProvider(Framebuffer framebuffer, Supplier<ShaderProgram> shader) {
        this.shader = shader;
        setFramebuffer(framebuffer);
    }

    public VertexConsumerProvider createDualProvider(VertexConsumerProvider parent, int red, int green, int blue, int alpha) {
        return layer -> {
            VertexConsumer parentBuffer = parent.getBuffer(layer);

            if (!(layer instanceof RenderLayer.MultiPhase)
                    || ((RenderLayer.MultiPhase) layer).getPhases().outlineMode == RenderLayer.OutlineMode.NONE) {
                return parentBuffer;
            }

            VertexConsumer plainBuffer = this.plainDrawer.getBuffer(
                    layerCreator.apply(((RenderLayer.MultiPhase) layer).getPhases().texture));
            return VertexConsumers.union(plainBuffer.color(red, green, blue, alpha), parentBuffer);
        };
    }

//    public VertexConsumerProvider createSingleProvider(VertexConsumerProvider parent, int red, int green, int blue, int alpha) {
//        return layer -> {
//            VertexConsumer parentBuffer = parent.getBuffer(layer);
//
//            if (!(layer instanceof RenderLayer.MultiPhase)
//                    || ((RenderLayer.MultiPhase) layer).getPhases().outlineMode == RenderLayer.OutlineMode.NONE) {
//                return parentBuffer;
//            }
//
//            VertexConsumer plainBuffer = this.plainDrawer.getBuffer(
//                    layerCreator.apply(((RenderLayer.MultiPhase) layer).getPhases().texture));
//            return new ColorVertexConsumer(plainBuffer, red, green, blue, alpha);
//        };
//    }

    public void setFramebuffer(Framebuffer framebuffer) {
        layerCreator = memoizeTexture(texture -> new RenderLayer(
                "kiwiclient_outline", VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS, 256, false, false,
                () -> {
                    texture.startDrawing();
                    RenderSystem.setShader(shader);
                    framebuffer.beginWrite(false);
                },
                () -> MinecraftClient.getInstance().getFramebuffer().beginWrite(false)) {});
    }

    private Function<TextureBase, RenderLayer> memoizeTexture(Function<TextureBase, RenderLayer> function) {
        return new Function<>() {
            private final Map<Identifier, RenderLayer> cache = new HashMap<>();

            public RenderLayer apply(TextureBase texture) {
                return this.cache.computeIfAbsent(texture.getId().get(), id -> function.apply(texture));
            }
        };
    }

    public void draw() {
        this.plainDrawer.draw();
    }
}