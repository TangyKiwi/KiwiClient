package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.util.render.CustomRenderPipelines;

import net.minecraft.client.renderer.ShaderManager;

@Mixin(ShaderManager.class)
public abstract class ShaderManagerMixin {
    @Inject(method = "apply", at = @At("TAIL"))
    private void reloadPipelines(CallbackInfo info) {
        CustomRenderPipelines.precompile();
    }
}