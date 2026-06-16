package com.tangykiwi.kiwiclient.mixin;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixininterface.IEntityRenderState;
import com.tangykiwi.kiwiclient.module.other.Deadmau5Ears;
import com.tangykiwi.kiwiclient.util.Textures;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

@Mixin(Deadmau5EarsLayer.class)
public abstract class Deadmau5EarsLayerMixin {
    @ModifyExpressionValue(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/AvatarRenderState;FF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/ClientAsset$Texture;texturePath()Lnet/minecraft/resources/Identifier;"))
    private Identifier modifyEarTexture(Identifier original, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, AvatarRenderState state, float yRot, float xRot) {
        Deadmau5Ears ears = (Deadmau5Ears) KiwiClient.moduleManager.getModule(Deadmau5Ears.class);
        if (ears.isEnabled() && ((IEntityRenderState) state).getEntity() instanceof Player player) {
            if (mc.player.getUUID().equals(player.getUUID())) {
                Identifier earTexture = Textures.EARS;
                return earTexture == null ? original : earTexture;
            }
        }
        return original;
    }
}
