package com.tangykiwi.kiwiclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.FullBright;
import com.tangykiwi.kiwiclient.module.render.NoRender;
import com.tangykiwi.kiwiclient.module.render.XRay;

import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(texture, ARGB.color(255, 255, 255, 255));
            profiler.pop();
            ci.cancel();
        }
    }

    // @Inject(method = "getDarkness", at = @At("HEAD"), cancellable = true)
	// private void getDarknessFactor(LivingEntity entity, float factor, float tickProgress, CallbackInfoReturnable<Float> info) {
	// 	if (KiwiClient.moduleManager.getModule(NoRender.class).getSetting("Darkness").asToggle().getValue()) info.setReturnValue(0.0f);
	// }
}
