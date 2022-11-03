package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.EntityRenderEvent;
import com.tangykiwi.kiwiclient.modules.combat.TargetHUD;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    public void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        EntityRenderEvent.Single.Label event = new EntityRenderEvent.Single.Label(entity, matrices, vertexConsumers);
        KiwiClient.eventBus.post(event);

        TargetHUD targetHUD = (TargetHUD) KiwiClient.moduleManager.getModule(TargetHUD.class);
        if (entity instanceof PlayerEntity && targetHUD.isEnabled() && targetHUD.playerEntity == entity) {
            info.cancel();
        }

        if (event.isCancelled()) {
            info.cancel();
        }
    }
}
