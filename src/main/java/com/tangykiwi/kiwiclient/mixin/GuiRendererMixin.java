package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.gui.render.GuiRenderer;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
    @ModifyArgs(
        method = "executeDraw(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V",
        at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setIndexBuffer(Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V")
    )
    private void fixNonQuadIndexing(Args args, @Local(argsOnly = true) GuiRenderer.Draw draw) {
        var pipeline = draw.pipeline();
        if (!pipeline.getLocation().getNamespace().equals("kiwiclient")) return;

        if (pipeline.getVertexFormatMode() != VertexFormat.Mode.QUADS) {
            var shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            args.set(0, shapeIndexBuffer.getBuffer(draw.indexCount()));
            args.set(1, shapeIndexBuffer.type());
        }
    }
}
