package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.FullBright;
import com.tangykiwi.kiwiclient.module.render.XRay;

import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.util.profiling.Profiler;

import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Lightmap.class)
public abstract class LightmapMixin {
    @Shadow
    @Final
    private GpuTexture texture;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void update$skip(LightmapRenderState renderState, CallbackInfo ci) {
        if (KiwiClient.moduleManager.getModule(FullBright.class).isEnabled() || KiwiClient.moduleManager.getModule(XRay.class).isEnabled()) {
            var profiler = Profiler.get();
            profiler.push("lightmap");
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(texture, new Vector4f(1));
            profiler.pop();
            ci.cancel();
        }
    }
}
