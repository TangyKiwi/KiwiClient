package com.tangykiwi.kiwiclient.util.cosmetics;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.mainmenu.MainMenu;
import com.tangykiwi.kiwiclient.modules.other.Deadmau5Ears;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class Deadmau5EarsRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public Deadmau5EarsRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l) {
        if (!(MinecraftClient.getInstance().currentScreen instanceof MainMenu) && KiwiClient.moduleManager.getModule(Deadmau5Ears.class).isEnabled() && !abstractClientPlayerEntity.isInvisible() && abstractClientPlayerEntity.getName().equals(MinecraftClient.getInstance().player.getName())) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(KiwiClient.EARS));
            int m = LivingEntityRenderer.getOverlay(abstractClientPlayerEntity, 0.0F);
            matrixStack.push();
            if (abstractClientPlayerEntity.isInSneakingPose()) {
                matrixStack.translate(0.0D, 0.25D, 0.0D);
            }

            this.getContextModel().renderEars(matrixStack, vertexConsumer, i, m);
            matrixStack.pop();
        }
    }
}
