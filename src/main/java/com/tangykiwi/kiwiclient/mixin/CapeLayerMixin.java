package com.tangykiwi.kiwiclient.mixin;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.other.Cape;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {
    @ModifyExpressionValue(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/PlayerSkin;cape()Lnet/minecraft/core/ClientAsset$Texture;"))
    private ClientAsset.Texture modifyCapeTexture(ClientAsset.Texture original, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        Cape cape = (Cape) KiwiClient.moduleManager.getModule(Cape.class);
        if (cape.isEnabled()) {
            Identifier capeTexture = cape.getCape();
            KiwiClient.LOGGER.info("enabled cape texture: " + capeTexture);
            return capeTexture != null ? original : new ClientAsset.ResourceTexture(capeTexture, capeTexture);
        }
        return original;
    }

    @Redirect(method = "submit", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;showCape:Z", opcode = Opcodes.GETFIELD))
    private boolean enableCapeRendering(AvatarRenderState original, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        Cape cape = (Cape) KiwiClient.moduleManager.getModule(Cape.class);
        if (cape.isEnabled()) {
            return true;
        }
        return original.showCape;
    }
}   
