package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.Nametags;
import com.tangykiwi.kiwiclient.util.EntityUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "hasLabel", at = @At("INVOKE"), cancellable = true)
	private void forceLabel(LivingEntity livingEntity, double d, CallbackInfoReturnable<Boolean> cir)
	{
        Nametags nametags = (Nametags) KiwiClient.moduleManager.getModule(Nametags.class);
		if (nametags.isEnabled()) {
            if (nametags.getSetting("Players").asToggle().getValue() && livingEntity instanceof PlayerEntity) {
				cir.setReturnValue(true);
			}
			if (nametags.getSetting("Animals").asToggle().getValue() && EntityUtils.isAnimal(livingEntity)) {
				cir.setReturnValue(true);
			}
        }
	}
}
