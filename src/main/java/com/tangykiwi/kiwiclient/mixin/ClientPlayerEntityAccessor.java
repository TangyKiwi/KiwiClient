package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {
    @Accessor("ticksSinceLastPositionPacketSent")
    void setTicksSinceLastPositionPacketSent(int ticks);
}