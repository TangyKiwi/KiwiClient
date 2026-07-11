package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.ESP;
import com.tangykiwi.kiwiclient.module.render.NoRender;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;

@Mixin(LevelExtractor.class)
public abstract class LevelExtractorMixin {
    @Unique
    private ESP esp;
    @Unique
    private NoRender noRender;

    @Inject(method = "setLevel", at = @At("TAIL"))
    private void onSetLevel(ClientLevel level, CallbackInfo ci) {
        esp = (ESP) KiwiClient.moduleManager.getModule(ESP.class);
        noRender = (NoRender) KiwiClient.moduleManager.getModule(NoRender.class);
    }

    @WrapWithCondition(
        method = "extract",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/WeatherEffectRenderer;extractRenderState(Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;)V"
        )
    )
    private boolean extractLevel$noWeather(
        WeatherEffectRenderer instance, ClientLevel level, float partialTicks, Vec3 cameraPos, WeatherRenderState renderState
    ) {
        if (noRender.isEnabled() && noRender.getSetting("Weather").asToggle().getValue()) {
            renderState.intensity = 0;
            return false;
        }
        return true;
    }

    @ModifyExpressionValue(
        method = "isEntityVisible",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/LevelRenderer;isSectionCompiledAndVisible(Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    private boolean isEntityVisible$forceRender(boolean original) {
        if (esp.isEnabled()) return true;
        return original;
    }
}
