package com.tangykiwi.kiwiclient.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.XRay;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ModelBlockRenderer.class)
public class ModelBlockRendererMixin {
    @Inject(method = {"tesselateFlat", "tesselateAmbientOcclusion"}, at = @At("HEAD"), cancellable = true)
    private void tesselate$xray(BlockQuadOutput output, float x, float y, float z, List<BlockStateModelPart> parts, BlockAndTintGetter level, BlockState state, BlockPos pos, CallbackInfo ci) {
        XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);

        if (xray.isEnabled() && !xray.blocks.contains(state.getBlock())) {
            ci.cancel();
        }
    }

    @ModifyReturnValue(method = "shouldRenderFace", at = @At("RETURN"))
    private static boolean shouldRenderFace$xray(boolean original, BlockAndTintGetter level, BlockState state, Direction direction, BlockPos neighborPos) {
        XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);

        if (xray.isEnabled()) {
            return xray.blocks.contains(state.getBlock());
            // return xray.modifyDrawSide(state, level, neighborPos.relative(direction.getOpposite()), direction, original);
        }

        return original;
    }
}
