package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;

// @Mixin(FluidRenderer.class)
// public class FluidRendererMixin {
//     @Inject(method = "render", at = @At("HEAD"), cancellable = true)
//     private void onRender(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo info) {
//         RenderFluidEvent event = new RenderFluidEvent(fluidState, pos, vertexConsumer);
//         KiwiClient.eventBus.post(event);

//         if (event.isCancelled()) info.cancel();
//     }
// }
