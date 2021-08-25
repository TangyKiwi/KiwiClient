package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.render.XRay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> callback) {
        XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);
        if (xray.getSetting(0).asToggle().state)
            return;

        if (xray.isEnabled() && !xray.isVisible(state.getBlockState().getBlock())) {
            callback.setReturnValue(false);
        }
    }

    @Inject(method = "isSideCovered(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;F)Z", at = @At("HEAD"), cancellable = true)
    private static void isSideCovered(BlockView world, BlockPos pos, Direction direction, float maxDeviation, CallbackInfoReturnable<Boolean> callback) {
        XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);

        if (!xray.getSetting(0).asToggle().state && xray.isVisible(world.getBlockState(pos).getBlock())) {
            callback.setReturnValue(false);
        }
    }
}
