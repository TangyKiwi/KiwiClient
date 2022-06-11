package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.random.Random;

public class RenderItemEntityEvent extends Event {
    public ItemEntity itemEntity;
    public float f;
    public float tickDelta;
    public MatrixStack matrixStack;
    public VertexConsumerProvider vertexConsumerProvider;
    public int light;
    public Random random;
    public ItemRenderer itemRenderer;

    public RenderItemEntityEvent (ItemEntity itemEntity, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, Random random, ItemRenderer itemRenderer) {
        this.setCancelled(false);
        this.itemEntity = itemEntity;
        this.f = f;
        this.tickDelta = tickDelta;
        this.matrixStack = matrixStack;
        this.vertexConsumerProvider = vertexConsumerProvider;
        this.light = light;
        this.random = random;
        this.itemRenderer = itemRenderer;
    }
}
