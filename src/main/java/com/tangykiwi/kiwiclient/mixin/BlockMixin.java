package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// @Mixin(Block.class)
// public class BlockMixin {
//     @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
//     private static void shouldDrawSide(BlockState state, BlockState otherState, Direction side, CallbackInfoReturnable<Boolean> callback) {
//         RenderBlockEvent.ShouldDrawSide event = new RenderBlockEvent.ShouldDrawSide(state);
//         KiwiClient.eventBus.post(event);

//         if (event.shouldDrawSide() != null) callback.setReturnValue(event.shouldDrawSide());
//     }
// }