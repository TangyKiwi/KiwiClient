package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.render.NoRender;

// @Mixin(SodiumWorldRenderer.class)
// public class SodiumWorldRendererMixin {
//     @ModifyVariable(method = "setupTerrain", at = @At("HEAD"), argsOnly = true)
//     private FogParameters modifyFogParameters(FogParameters fogParameters) {
//         if (KiwiClient.moduleManager.getModule(NoRender.class).getSetting("Fog").asToggle().getValue()) return FogParameters.NONE;

//         return fogParameters;
//     }
// }
