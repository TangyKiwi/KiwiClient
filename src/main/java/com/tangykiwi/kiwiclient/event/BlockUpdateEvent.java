package com.tangykiwi.kiwiclient.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUpdateEvent extends Event {
    public BlockPos pos;
    public BlockState oldState, newState;

    public BlockUpdateEvent(BlockPos pos, BlockState oldState, BlockState newState) {
        this.pos = pos;
        this.oldState = oldState;
        this.newState = newState;
    }
}
