package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.mixininterface.IPlayerInteractEntityC2SPacket;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.tangykiwi.kiwiclient.util.Utils.mc;

@Mixin(PlayerInteractEntityC2SPacket.class)
public abstract class PlayerInteractEntityC2SPacketMixin implements IPlayerInteractEntityC2SPacket {
    @Shadow
    @Final
    private PlayerInteractEntityC2SPacket.InteractTypeHandler type;
    @Shadow
    @Final
    private int entityId;

    @Override
    public PlayerInteractEntityC2SPacket.InteractType getType() {
        return type.getType();
    }

    @Override
    public Entity getEntity() {
        return mc.world.getEntityById(entityId);
    }
}
