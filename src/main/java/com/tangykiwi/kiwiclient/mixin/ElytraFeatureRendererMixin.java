package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.other.Cape;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

// @Mixin(ElytraFeatureRenderer.class)
// public class ElytraFeatureRendererMixin {
//     @ModifyExpressionValue(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/ElytraFeatureRenderer;getTexture(Lnet/minecraft/client/render/entity/state/BipedEntityRenderState;)Lnet/minecraft/util/Identifier;"))
//     private Identifier modifyCapeTexture(Identifier original, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, BipedEntityRenderState bipedEntityRenderState, float f, float g) {
//         Cape cape = (Cape) KiwiClient.moduleManager.getModule(Cape.class);
//         if (cape.isEnabled() && bipedEntityRenderState.displayName != null && bipedEntityRenderState.displayName.equals(mc.player.getDisplayName())) {
//             return cape.getCape();
//         }
//         return original;
//     }
// }
