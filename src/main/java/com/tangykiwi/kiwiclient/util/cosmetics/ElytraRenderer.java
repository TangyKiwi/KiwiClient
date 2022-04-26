package com.tangykiwi.kiwiclient.util.cosmetics;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.other.Cape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class ElytraRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private static final Identifier SKIN = new Identifier("textures/entity/elytra.png");
    private final ElytraEntityModel<AbstractClientPlayerEntity> elytra;

    public ElytraRenderer(FeatureRendererContext context, EntityModelLoader loader) {
        super(context);
        this.elytra = new ElytraEntityModel(loader.getModelPart(EntityModelLayers.ELYTRA));
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
        if (itemStack.getItem() == Items.ELYTRA) {
            Identifier resourcelocation;
            if (KiwiClient.moduleManager.getModule(Cape.class).isEnabled() && ((Cape) KiwiClient.moduleManager.getModule(Cape.class)).getCape() != null && livingEntity.isPartVisible(PlayerModelPart.CAPE) && livingEntity.getName().equals(MinecraftClient.getInstance().player.getName())) {
                resourcelocation = ((Cape) KiwiClient.moduleManager.getModule(Cape.class)).getCape();
            } else {
                resourcelocation = SKIN;
            }

            matrixStack.push();
            matrixStack.translate(0.0D, 0.0D, 0.125D);
            this.getContextModel().copyStateTo(this.elytra);
            this.elytra.setAngles(livingEntity, f, g, j, k, l);
            VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(resourcelocation), false, itemStack.hasGlint());
            this.elytra.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.pop();
        }

    }
}
