package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.other.Cape;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Mixin(CapeFeatureRenderer.class)
public class CapeFeatureRendererMixin {
    @ModifyExpressionValue(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private VertexConsumer modifyCapeRenderer(VertexConsumer original, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
        Cape cape = (Cape) KiwiClient.moduleManager.getModule(Cape.class);
        if (cape.isEnabled()) {
            return ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getEntitySolid(cape.getCape()), cape.getSetting("Glint").asToggle().getValue());
        }
        return original;
    }

    // @ModifyExpressionValue(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SkinTextures;capeTexture()Lnet/minecraft/util/Identifier;"))
    // private Identifier modifyCapeTexture(Identifier original, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, PlayerEntityRenderState playerEntityRenderState, float f, float g) {
    //     Cape cape = (Cape) KiwiClient.moduleManager.getModule(Cape.class);
    //     if (cape.isEnabled()) {
    //         return cape.getCape();
    //     }
    //     return original;
    // }
}
