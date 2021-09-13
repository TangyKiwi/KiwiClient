package com.tangykiwi.kiwiclient.event;

public class WorldRenderEvent extends Event {
    protected float partialTicks;

    public static class Pre extends WorldRenderEvent {

        public Pre(float partialTicks) {
            this.partialTicks = partialTicks;
        }

    }

    public static class Post extends WorldRenderEvent {

        public Post(float partialTicks) {
            this.partialTicks = partialTicks;
        }

    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
