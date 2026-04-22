package com.tangykiwi.kiwiclient.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;

// @Mixin(BlockModelRenderer.class)
// public class BlockModelRendererMixin {
//     @Inject(method = "render", at = @At("HEAD")) 
//     public void render(BlockRenderView world, List<BlockModelPart> parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay, CallbackInfo ci) {
//         RenderBlockEvent.Tesselate event = new RenderBlockEvent.Tesselate(state, pos, matrices, vertexConsumer);
//         KiwiClient.eventBus.post(event);
//     }
// }
