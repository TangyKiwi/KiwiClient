package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.tangykiwi.kiwiclient.mixininterface.IServerboundMovePlayerPacket;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

@Mixin(ServerboundMovePlayerPacket.class)
public class ServerboundMovePlayerPacketMixin implements IServerboundMovePlayerPacket {
    @Unique private int tag = 0;

    @Override
    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    public int getTag() {
        return tag;
    }
    
}
