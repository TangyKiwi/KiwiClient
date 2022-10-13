package com.tangykiwi.kiwiclient.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityUtils {

    public static boolean isAnimal(Entity e) {
        return e instanceof PassiveEntity
            || e instanceof AmbientEntity
            || e instanceof WaterCreatureEntity
            || e instanceof IronGolemEntity
            || e instanceof SnowGolemEntity;
    }

    public static boolean isMob(Entity e) {
        return e instanceof Monster;
    }

    public static boolean isPlayer(Entity e) {
        return e instanceof PlayerEntity;
    }

    public static boolean isOtherServerPlayer(Entity e) {
        return e instanceof PlayerEntity
            && e != MinecraftClient.getInstance().player
            && !(e instanceof PlayerCopyEntity);
    }

    public static boolean isAttackable(Entity e, boolean ignoreFriends) {
        return e instanceof LivingEntity
            && e.isAlive()
            && e != MinecraftClient.getInstance().player
            && !e.isConnectedThroughVehicle(MinecraftClient.getInstance().player)
            && !(e instanceof PlayerCopyEntity)
            && (!ignoreFriends);
    }

    public static int getPing(PlayerEntity player) {
        if (Utils.mc.getNetworkHandler() == null) return 0;

        PlayerListEntry playerListEntry = Utils.mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }
}
