package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.util.math.MatrixStack;

public class WorldRenderEvent extends Event {
    protected float partialTicks;
    protected MatrixStack matrixStack;

    public static class Pre extends WorldRenderEvent {

        public Pre(float partialTicks) {
            this.partialTicks = partialTicks;
        }

        public Pre(float partialTicks, MatrixStack matrixStack) {
            this.partialTicks = partialTicks;
            this.matrixStack = matrixStack;
        }

    }

    public static class Post extends WorldRenderEvent {

        public Post(float partialTicks) {
            this.partialTicks = partialTicks;
        }

        public Post(float partialTicks, MatrixStack matrixStack) {
            this.partialTicks = partialTicks;
            this.matrixStack = matrixStack;
        }

    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public MatrixStack getMatrixStack() { return matrixStack; }
}
