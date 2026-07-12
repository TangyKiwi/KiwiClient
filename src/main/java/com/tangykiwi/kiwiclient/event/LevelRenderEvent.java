package com.tangykiwi.kiwiclient.event;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeStorage;

public class LevelRenderEvent extends Event {
    private final PoseStack poseStack;
    private final float partialTicks;
    private final SubmitNodeStorage submitNodeStorage;

    public LevelRenderEvent(PoseStack poseStack, float partialTicks, SubmitNodeStorage submitNodeStorage) {
        this.poseStack = poseStack;
        this.partialTicks = partialTicks;
        this.submitNodeStorage = submitNodeStorage;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public SubmitNodeStorage getSubmitNodeStorage() {
        return submitNodeStorage;
    }
}
