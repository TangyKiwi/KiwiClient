package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.util.CapeRenderer;
import com.tangykiwi.kiwiclient.util.Deadmau5EarsRenderer;
import com.tangykiwi.kiwiclient.util.ElytraRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ListIterator;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context entityRenderDispatcher_1, PlayerEntityModel<AbstractClientPlayerEntity> entityModel_1, float float_1) {
        super(entityRenderDispatcher_1, entityModel_1, float_1);
    }

    @Inject(
            method = {"<init>*"},
            at = {@At("RETURN")}
    )
    private void construct(EntityRendererFactory.Context ctm, boolean alex, CallbackInfo info) {
        this.addFeature(new CapeRenderer(this));
        this.addFeature(new Deadmau5EarsRenderer(this));
        this.addFeature(new ElytraRenderer(this, ctm.getModelLoader()));

        this.features.removeIf((modelFeature) -> {
            return modelFeature instanceof ElytraFeatureRenderer;
        });
        this.features.removeIf((modelFeature) -> {
            return modelFeature instanceof CapeFeatureRenderer;
        });
    }
}
