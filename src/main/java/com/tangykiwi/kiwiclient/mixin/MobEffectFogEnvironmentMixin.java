package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.module.Module;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.NoRender;

import net.minecraft.client.renderer.fog.environment.MobEffectFogEnvironment;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEffectFogEnvironment.class)
public abstract class MobEffectFogEnvironmentMixin {
    @Shadow
    public abstract Holder<MobEffect> getMobEffect();

    @ModifyReturnValue(method = "isApplicable", at = @At("RETURN"))
    private boolean modifyShouldApply(boolean original) {
        Module noRender = KiwiClient.moduleManager.getModule(NoRender.class);
        if (getMobEffect() == MobEffects.BLINDNESS) return original && !noRender.getSetting("Blindness").asToggle().getValue();
        if (getMobEffect() == MobEffects.DARKNESS) return original && !noRender.getSetting("Darkness").asToggle().getValue();
        return original;
    }
}