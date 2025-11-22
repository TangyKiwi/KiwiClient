package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.module.Module;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.NoRender;

import net.minecraft.client.render.fog.StatusEffectFogModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StatusEffectFogModifier.class)
public abstract class StatusEffectFogModifierMixin {
    @Shadow
    public abstract RegistryEntry<StatusEffect> getStatusEffect();

    @ModifyReturnValue(method = "shouldApply", at = @At("RETURN"))
    private boolean modifyShouldApply(boolean original) {
        Module noRender = KiwiClient.moduleManager.getModule(NoRender.class);
        if (getStatusEffect() == StatusEffects.BLINDNESS) return original && !noRender.getSetting("Blindness").asToggle().getValue();
        if (getStatusEffect() == StatusEffects.DARKNESS) return original && !noRender.getSetting("Darkness").asToggle().getValue();
        return original;
    }
}