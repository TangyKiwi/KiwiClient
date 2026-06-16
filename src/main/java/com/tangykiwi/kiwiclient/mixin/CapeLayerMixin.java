package com.tangykiwi.kiwiclient.mixin;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixininterface.IEntityRenderState;
import com.tangykiwi.kiwiclient.module.other.Cape;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin {
    @ModifyExpressionValue(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/PlayerSkin;cape()Lnet/minecraft/core/ClientAsset$Texture;"))
    private ClientAsset.Texture modifyCapeTexture(ClientAsset.Texture original, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        Cape cape = (Cape) KiwiClient.moduleManager.getModule(Cape.class);
        if (cape.isEnabled() && ((IEntityRenderState) state).getEntity() instanceof Player player) {
            if (mc.player.getUUID().equals(player.getUUID())) {
                Identifier capeTexture = cape.getCape();
                return capeTexture == null ? original : new ClientAsset.ResourceTexture(capeTexture, capeTexture);
            }
        }
        return original;
    }
}   
