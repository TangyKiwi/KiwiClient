package com.tangykiwi.kiwiclient.util;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class EntityUtils {
    public static boolean isAnimal(EntityType<?> e) {
        return e == EntityTypes.ALLAY
            || e == EntityTypes.ARMADILLO
            || e == EntityTypes.BAT
            || e == EntityTypes.BEE
            || e == EntityTypes.CAMEL
            || e == EntityTypes.CAT
            || e == EntityTypes.CHICKEN
            || e == EntityTypes.COD
            || e == EntityTypes.COW
            || e == EntityTypes.DOLPHIN
            || e == EntityTypes.DONKEY
            || e == EntityTypes.FOX
            || e == EntityTypes.FROG
            || e == EntityTypes.GLOW_SQUID
            || e == EntityTypes.GOAT
            || e == EntityTypes.HAPPY_GHAST
            || e == EntityTypes.HORSE
            || e == EntityTypes.IRON_GOLEM
            || e == EntityTypes.LLAMA
            || e == EntityTypes.MOOSHROOM
            || e == EntityTypes.MULE
            || e == EntityTypes.OCELOT
            || e == EntityTypes.PANDA
            || e == EntityTypes.PARROT
            || e == EntityTypes.PIG
            || e == EntityTypes.POLAR_BEAR
            || e == EntityTypes.PUFFERFISH
            || e == EntityTypes.RABBIT
            || e == EntityTypes.SALMON
            || e == EntityTypes.SHEEP
            || e == EntityTypes.SNIFFER
            || e == EntityTypes.SNOW_GOLEM
            || e == EntityTypes.SQUID
            || e == EntityTypes.STRIDER
            || e == EntityTypes.TADPOLE
            || e == EntityTypes.TRADER_LLAMA
            || e == EntityTypes.TROPICAL_FISH
            || e == EntityTypes.TURTLE
            || e == EntityTypes.VILLAGER
            || e == EntityTypes.WANDERING_TRADER
            || e == EntityTypes.WOLF;
    }

    public static boolean isAnimal(Entity e) {
        return e instanceof Animal;
    }

    public static boolean isMob(EntityType<?> e) {
        return e == EntityTypes.BLAZE
            || e == EntityTypes.BOGGED
            || e == EntityTypes.BREEZE
            || e == EntityTypes.CAVE_SPIDER
            || e == EntityTypes.CREAKING
            || e == EntityTypes.CREEPER
            || e == EntityTypes.DROWNED
            || e == EntityTypes.ELDER_GUARDIAN
            || e == EntityTypes.ENDERMAN
            || e == EntityTypes.ENDERMITE
            || e == EntityTypes.EVOKER
            || e == EntityTypes.GHAST
            || e == EntityTypes.GUARDIAN
            || e == EntityTypes.HOGLIN
            || e == EntityTypes.HUSK
            || e == EntityTypes.ILLUSIONER
            || e == EntityTypes.MAGMA_CUBE
            || e == EntityTypes.PHANTOM
            || e == EntityTypes.PIGLIN
            || e == EntityTypes.PIGLIN_BRUTE
            || e == EntityTypes.RAVAGER
            || e == EntityTypes.SHULKER
            || e == EntityTypes.SILVERFISH
            || e == EntityTypes.SKELETON
            || e == EntityTypes.SKELETON_HORSE
            || e == EntityTypes.SLIME
            || e == EntityTypes.SPIDER
            || e == EntityTypes.STRAY
            || e == EntityTypes.VEX
            || e == EntityTypes.VINDICATOR
            || e == EntityTypes.WARDEN
            || e == EntityTypes.WITCH
            || e == EntityTypes.WITHER_SKELETON
            || e == EntityTypes.ZOGLIN
            || e == EntityTypes.ZOMBIE
            || e == EntityTypes.ZOMBIE_HORSE
            || e == EntityTypes.ZOMBIE_VILLAGER
            || e == EntityTypes.ZOMBIFIED_PIGLIN;
    }

    public static boolean isMob(Entity e) {
        return e instanceof Monster;
    }

    public static boolean isPlayer(Entity e) {
        return e instanceof Player;
    }

    public static boolean isOtherServerPlayer(Entity e) {
        return e instanceof Player
            && e != mc.player;
    }

    public static int getPing(Player player) {
        if (mc.getConnection() == null) return 0;

        PlayerInfo playerListEntry = mc.getConnection().getPlayerInfo(player.getUUID());
        if (playerListEntry == null) return 0;
        return playerListEntry.getLatency();
    }

    // public static String getEnchantmentName(Enchantment enchantment) {
    //     String text = enchantment.description().getString();
    //     if(text.contains("aqua")) return "AqAf";
    //     if(text.contains("bane")) return "BnAr";
    //     if(text.contains("blas")) return "BlPr";
    //     if(text.contains("chan")) return "Chnl";
    //     if(text.contains("bind")) return "CuBi";
    //     if(text.contains("vani")) return "CuVa";
    //     if(text.contains("dept")) return "DStr";
    //     if(text.contains("effi")) return "Effi";
    //     if(text.contains("feat")) return "FeFa";
    //     if(text.contains("aspe")) return "FAsp";
    //     if(text.contains("fire")) return "FiPr";
    //     if(text.contains("flam")) return "Flme";
    //     if(text.contains("fort")) return "Fort";
    //     if(text.contains("fros")) return "FrWa";
    //     if(text.contains("impa")) return "Impl";
    //     if(text.contains("infi")) return "Infi";
    //     if(text.contains("knoc")) return "KnBa";
    //     if(text.contains("loot")) return "Loot";
    //     if(text.contains("loya")) return "Llty";
    //     if(text.contains("luck")) return "Luck";
    //     if(text.contains("mend")) return "Mend";
    //     if(text.contains("mult")) return "Mult";
    //     if(text.contains("pier")) return "Pier";
    //     if(text.contains("powe")) return "Powe";
    //     if(text.contains("proj")) return "PrPr";
    //     if(text.contains("prot")) return "Prot";
    //     if(text.contains("punc")) return "Pnch";
    //     if(text.contains("quic")) return "Chrg";
    //     if(text.contains("resp")) return "Resp";
    //     if(text.contains("ript")) return "Rptd";
    //     if(text.contains("shar")) return "Shrp";
    //     if(text.contains("silk")) return "Silk";
    //     if(text.contains("smit")) return "Smte";
    //     if(text.contains("soul")) return "SSpd";
    //     if(text.contains("swee")) return "SwpE";
    //     if(text.contains("swif")) return "SwSn";
    //     if(text.contains("thor")) return "Thrn";
    //     if(text.contains("unbr")) return "Unbr";
    //     return "NaN";
    // }
}