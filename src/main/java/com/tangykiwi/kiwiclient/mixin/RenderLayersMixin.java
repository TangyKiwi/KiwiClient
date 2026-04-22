package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// @Mixin(RenderLayers.class)
// public class RenderLayersMixin {
//     @Inject(method = "getBlockLayer", at = @At("HEAD"), cancellable = true)
//     private static void getBlockLayer(BlockState state, CallbackInfoReturnable<BlockRenderLayer> callback) {
//         RenderBlockEvent.Layer event = new RenderBlockEvent.Layer(state);
//         KiwiClient.eventBus.post(event);

//         if (event.getLayer() != null) callback.setReturnValue(event.getLayer());
//     }
// }
