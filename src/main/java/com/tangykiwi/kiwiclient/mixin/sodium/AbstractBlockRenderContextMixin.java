package com.tangykiwi.kiwiclient.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.XRay;

import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = AbstractBlockRenderContext.class, remap = false)
public abstract class AbstractBlockRenderContextMixin {
    @Shadow
	protected BlockState state;

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private void onShouldDrawSide(Direction face, CallbackInfoReturnable<Boolean> cir) {
        XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);

        if (xray.isEnabled()) {
            cir.setReturnValue(xray.blocks.contains(state.getBlock()));
            // return xray.modifyDrawSide(state, level, neighborPos.relative(direction.getOpposite()), direction, original);
        }
    }
}
