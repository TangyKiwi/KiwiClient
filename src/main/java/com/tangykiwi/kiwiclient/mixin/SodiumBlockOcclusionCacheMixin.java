package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// @Mixin(value=BlockOcclusionCache.class, remap = false)
// public class SodiumBlockOcclusionCacheMixin {
//     @Inject(method = "shouldDrawSide", at = @At("RETURN"), cancellable = true, remap = false)
//     public void shouldDrawSide(BlockState state, BlockView view, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> info) {
//         RenderBlockEvent.ShouldDrawSide event = new RenderBlockEvent.ShouldDrawSide(state);
//         KiwiClient.eventBus.post(event);

//         if (event.shouldDrawSide() != null) info.setReturnValue(event.shouldDrawSide());
//     }
// }
