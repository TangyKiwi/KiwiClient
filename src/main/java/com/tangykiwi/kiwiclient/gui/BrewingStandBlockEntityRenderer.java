package com.tangykiwi.kiwiclient.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;

@Environment(EnvType.CLIENT)
public class BrewingStandBlockEntityRenderer extends BlockEntityRenderer<BrewingStandBlockEntity> {

    public BrewingStandBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }


    @Override
    public void render(BrewingStandBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int lightAtBlock = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos());
        ItemStack item1 = blockEntity.getStack(0);
        ItemStack item2 = blockEntity.getStack(1);
        ItemStack item3 = blockEntity.getStack(2);

        matrices.push();

        matrices.translate(0.86f, 0.23f, 0.5f);
        matrices.scale(1.15f, 1.15f, 1.15f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
        MinecraftClient.getInstance().getItemRenderer().renderItem(item1, ModelTransformation.Mode.GROUND, lightAtBlock, overlay, matrices, vertexConsumers);

        matrices.pop();
        matrices.push();

        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(315));
        matrices.translate(0.32f, 0.23f, 0f);
        matrices.scale(1.15f, 1.15f, 1.15f);
        MinecraftClient.getInstance().getItemRenderer().renderItem(item2, ModelTransformation.Mode.GROUND, lightAtBlock, overlay, matrices, vertexConsumers);

        matrices.pop();
        matrices.push();

        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(45));
        matrices.translate(-0.39f, 0.23f, 0.705f);
        matrices.scale(1.15f, 1.15f, 1.15f);
        MinecraftClient.getInstance().getItemRenderer().renderItem(item3, ModelTransformation.Mode.GROUND, lightAtBlock, overlay, matrices, vertexConsumers);

        matrices.pop();
    }
}
