package com.tangykiwi.kiwiclient.event;

import net.minecraft.block.BlockState;

public class BlockActivateEvent extends Event {
    public BlockState blockState;

    public BlockActivateEvent (BlockState blockState) {
        this.blockState = blockState;
    }
}
