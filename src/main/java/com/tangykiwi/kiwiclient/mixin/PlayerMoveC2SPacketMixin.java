package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import com.tangykiwi.kiwiclient.mixininterface.IPlayerMoveC2SPacket;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(PlayerMoveC2SPacket.class)
public class PlayerMoveC2SPacketMixin implements IPlayerMoveC2SPacket {
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
