package com.tangykiwi.kiwiclient.event;

import com.mojang.blaze3d.vertex.PoseStack;

public class LevelRenderEvent extends Event {
    private final PoseStack poseStack;
    private final float partialTicks;

    public LevelRenderEvent(PoseStack poseStack, float partialTicks) {
        this.poseStack = poseStack;
        this.partialTicks = partialTicks;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
