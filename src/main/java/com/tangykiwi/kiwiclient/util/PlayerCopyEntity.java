package com.tangykiwi.kiwiclient.util;

import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerCopyEntity extends OtherClientPlayerEntity {

    private boolean ghost;

    public PlayerCopyEntity() {
        this(MinecraftClient.getInstance().player);
    }

    public PlayerCopyEntity(PlayerEntity player) {
        this(player, player.getX(), player.getY(), player.getZ());
    }

    public PlayerCopyEntity(PlayerEntity player, double x, double y, double z) {
        super(MinecraftClient.getInstance().world, player.getGameProfile(), MinecraftClient.getInstance().player.getPublicKey());

        copyFrom(player);

        // Cache the player textures, then switch to a random uuid
        // because the world doesn't allow duplicate uuids in 1.17+
        getPlayerListEntry();
        dataTracker.set(PLAYER_MODEL_PARTS, player.getDataTracker().get(PLAYER_MODEL_PARTS));
        setUuid(UUID.randomUUID());
    }

    public void spawn() {
        unsetRemoved();
        MinecraftClient.getInstance().world.addEntity(this.getId(), this);
    }

    public void despawn() {
        MinecraftClient.getInstance().world.removeEntity(this.getId(), RemovalReason.DISCARDED);
    }

    public void setGhost(boolean ghost) {
        this.ghost = ghost;
    }

    @Override
    public boolean isInvisible() {
        return ghost ? true : super.isInvisible();
    }

    @Override
    public boolean isInvisibleTo(PlayerEntity player) {
        return ghost ? false : super.isInvisibleTo(player);
    }
}