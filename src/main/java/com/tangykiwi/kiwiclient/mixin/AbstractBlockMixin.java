package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// @Mixin(AbstractBlock.class)
// public class AbstractBlockMixin {
//     @Inject(method = "getAmbientOcclusionLightLevel", at = @At("HEAD"), cancellable = true)
//     private void onGetAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> info) {
//         RenderBlockEvent.Light event = new RenderBlockEvent.Light(state);
//         KiwiClient.eventBus.post(event);

//         if (event.getLight() != null) info.setReturnValue(event.getLight());
//     }
// }
