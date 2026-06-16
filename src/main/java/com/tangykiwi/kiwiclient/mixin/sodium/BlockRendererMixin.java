package com.tangykiwi.kiwiclient.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.XRay;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = BlockRenderer.class, remap = false)
public abstract class BlockRendererMixin {
    @Inject(method = "renderModel", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/model/color/ColorProviderRegistry;getColorProvider(Lnet/minecraft/world/level/block/Block;)Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;", remap = true), cancellable = true)
	public void onRenderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
         XRay xray = (XRay) KiwiClient.moduleManager.getModule(XRay.class);

        if (xray.isEnabled() && !xray.blocks.contains(state.getBlock())) {
            ci.cancel();
        }
    }   
}
