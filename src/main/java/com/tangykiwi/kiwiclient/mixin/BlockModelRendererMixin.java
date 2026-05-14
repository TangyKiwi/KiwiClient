package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.XRay;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ModelBlockRenderer.class)
public class BlockModelRendererMixin {
    @ModifyReturnValue(method = "shouldRenderFace", at = @At("RETURN"))
    private static boolean shouldRenderFace$xray(boolean original, BlockAndTintGetter level, BlockState state, Direction direction, BlockPos neighborPos) {
        XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);

        if (xray.isEnabled()) {
            return xray.modifyDrawSide(state, level, neighborPos.relative(direction.getOpposite()), direction, original);
        }

        return original;
    }
}
