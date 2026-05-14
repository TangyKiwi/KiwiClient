package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.XRay;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void shouldRenderFace(BlockState state, BlockState neighborState, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);

        if (xray.isEnabled()) {
            cir.setReturnValue(xray.blocks.contains(state.getBlock()));
            // return xray.modifyDrawSide(state, level, neighborPos.relative(direction.getOpposite()), direction, original);
        }
    }
}
