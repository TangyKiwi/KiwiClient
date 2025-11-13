package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.other.Deadmau5Ears;
import com.tangykiwi.kiwiclient.util.Textures;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Mixin(Deadmau5FeatureRenderer.class)
public class Deadmau5FeatureRendererMixin {
    @ModifyExpressionValue(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;name:Ljava/lang/String;"))
    private String enableEarRendering(String original, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
        if (KiwiClient.moduleManager.getModule(Deadmau5Ears.class).isEnabled()) {
            return "deadmau5";
        }
        return original;
    }

    @ModifyExpressionValue(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SkinTextures;texture()Lnet/minecraft/util/Identifier;"))
    private Identifier modifyEarTexture(Identifier original, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
        if (KiwiClient.moduleManager.getModule(Deadmau5Ears.class).isEnabled()) {
            return Textures.EARS;
        }
        return original;
    }
}
