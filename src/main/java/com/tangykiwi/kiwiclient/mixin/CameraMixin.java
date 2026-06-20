package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.Freecam;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow
	private boolean initialized;

	@Shadow
	private Entity entity;

	@Shadow
	private Level level;

	@Inject(at = {
			@At("HEAD") }, method = "setEntity(Lnet/minecraft/world/entity/Entity;)V", cancellable = true)
	private void onSetEntity(Entity entity, CallbackInfo ci) {
		if (this.entity != null && KiwiClient.moduleManager.getModule(Freecam.class).isEnabled()) {
			ci.cancel();
		}
	}

	@Inject(at = { @At("HEAD") }, method = "alignWithEntity(F)V", cancellable = true)
	private void onAlignWithEntity(float partialTicks, CallbackInfo ci) {
		if (this.entity != null && KiwiClient.moduleManager.getModule(Freecam.class).isEnabled()) {
			ci.cancel();
		}
	}

	@Inject(at = { @At("HEAD") }, method = "isDetached()Z", cancellable = true)
	private void onIsDetached(CallbackInfoReturnable<Boolean> cir) {
		if (KiwiClient.moduleManager.getModule(Freecam.class).isEnabled()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = {
			@At("HEAD") }, method = "getFluidInCamera()Lnet/minecraft/world/level/material/FogType;", cancellable = true)
	private void onGetSubmersionType(CallbackInfoReturnable<FogType> cir) {
		if (KiwiClient.moduleManager.getModule(Freecam.class).isEnabled()) {
			cir.setReturnValue(FogType.NONE);
		}
	}
}
