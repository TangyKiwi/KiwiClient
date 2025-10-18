package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.RenderBlockEvent;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
    @Inject(method = "isOpaque", at = @At("HEAD"), cancellable = true)
    public void isOpaque(CallbackInfoReturnable<Boolean> callback) {
        RenderBlockEvent.Opaque event = new RenderBlockEvent.Opaque((BlockState) (Object) this);
        KiwiClient.eventBus.post(event);

        if (event.isOpaque() != null) callback.setReturnValue(event.isOpaque());
    }
}
