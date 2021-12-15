package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;

public class RenderFluidEvent extends Event {

    private FluidState state;
    private BlockPos pos;
    private VertexConsumer vertexConsumer;

    public RenderFluidEvent(FluidState state, BlockPos pos, VertexConsumer vertexConsumer) {
        this.state = state;
        this.pos = pos;
        this.vertexConsumer = vertexConsumer;
    }

    public FluidState getState() {
        return state;
    }

    public BlockPos getPos() {
        return pos;
    }

    public VertexConsumer getVertexConsumer() {
        return vertexConsumer;
    }
}

