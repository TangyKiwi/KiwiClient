package com.tangykiwi.kiwiclient.event;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class RenderBlockEvent extends Event {

    private BlockState state;

    public RenderBlockEvent(BlockState state) {
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }

    public static class Light extends RenderBlockEvent {

        private Float light;

        public Light(BlockState state) {
            super(state);
        }

        public Float getLight() {
            return light;
        }

        public void setLight(float light) {
            this.light = light;
        }
    }

    public static class Opaque extends RenderBlockEvent {

        private Boolean opaque;

        public Opaque(BlockState state) {
            super(state);
        }

        public Boolean isOpaque() {
            return opaque;
        }

        public void setOpaque(boolean opaque) {
            this.opaque = opaque;
        }
    }

    public static class ShouldDrawSide extends RenderBlockEvent {

        private Boolean drawSide;

        public ShouldDrawSide(BlockState state) {
            super(state);
        }

        public Boolean shouldDrawSide() {
            return drawSide;
        }

        public void setDrawSide(boolean drawSide) {
            this.drawSide = drawSide;
        }
    }

    public static class Tesselate extends RenderBlockEvent {

        private BlockPos pos;
        private MatrixStack matrices;
        private VertexConsumer vertexConsumer;

        public Tesselate(BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer) {
            super(state);
            this.pos = pos;
            this.matrices = matrices;
            this.vertexConsumer = vertexConsumer;
        }

        public BlockPos getPos() {
            return pos;
        }

        public MatrixStack getMatrices() {
            return matrices;
        }

        public VertexConsumer getVertexConsumer() {
            return vertexConsumer;
        }
    }

    public static class Layer extends RenderBlockEvent {

        private RenderLayer layer;

        public Layer(BlockState state) {
            super(state);
        }

        public RenderLayer getLayer() {
            return layer;
        }

        public void setLayer(RenderLayer layer) {
            this.layer = layer;
        }
    }
}