package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.Nametags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.mob.MobEntity;

@Mixin(MobEntityRenderer.class)
public class MobEntityRendererMixin {
	@Inject(method = "hasLabel", at = @At("INVOKE"), cancellable = true)
	private void forceLabel(MobEntity mobEntity, double d, CallbackInfoReturnable<Boolean> cir)
	{
        Nametags nametags = (Nametags) KiwiClient.moduleManager.getModule(Nametags.class);
		if (nametags.isEnabled() && nametags.getSetting("Mobs").asToggle().getValue()) {
            cir.setReturnValue(true);
        }
	}
}
