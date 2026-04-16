package com.tangykiwi.kiwiclient.util;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

public class EntityUtils {
    public static boolean isAnimal(EntityType<?> e) {
        return e == EntityType.ALLAY
            || e == EntityType.ARMADILLO
            || e == EntityType.BAT
            || e == EntityType.BEE
            || e == EntityType.CAMEL
            || e == EntityType.CAT
            || e == EntityType.CHICKEN
            || e == EntityType.COD
            || e == EntityType.COW
            || e == EntityType.DOLPHIN
            || e == EntityType.DONKEY
            || e == EntityType.FOX
            || e == EntityType.FROG
            || e == EntityType.GLOW_SQUID
            || e == EntityType.GOAT
            || e == EntityType.HAPPY_GHAST
            || e == EntityType.HORSE
            || e == EntityType.IRON_GOLEM
            || e == EntityType.LLAMA
            || e == EntityType.MOOSHROOM
            || e == EntityType.MULE
            || e == EntityType.OCELOT
            || e == EntityType.PANDA
            || e == EntityType.PARROT
            || e == EntityType.PIG
            || e == EntityType.POLAR_BEAR
            || e == EntityType.PUFFERFISH
            || e == EntityType.RABBIT
            || e == EntityType.SALMON
            || e == EntityType.SHEEP
            || e == EntityType.SNIFFER
            || e == EntityType.SNOW_GOLEM
            || e == EntityType.SQUID
            || e == EntityType.STRIDER
            || e == EntityType.TADPOLE
            || e == EntityType.TRADER_LLAMA
            || e == EntityType.TROPICAL_FISH
            || e == EntityType.TURTLE
            || e == EntityType.VILLAGER
            || e == EntityType.WANDERING_TRADER
            || e == EntityType.WOLF;
    }

    public static boolean isAnimal(Entity e) {
        return e instanceof Animal;
    }

    public static boolean isMob(EntityType<?> e) {
        return e == EntityType.BLAZE
            || e == EntityType.BOGGED
            || e == EntityType.BREEZE
            || e == EntityType.CAVE_SPIDER
            || e == EntityType.CREAKING
            || e == EntityType.CREEPER
            || e == EntityType.DROWNED
            || e == EntityType.ELDER_GUARDIAN
            || e == EntityType.ENDERMAN
            || e == EntityType.ENDERMITE
            || e == EntityType.EVOKER
            || e == EntityType.GHAST
            || e == EntityType.GUARDIAN
            || e == EntityType.HOGLIN
            || e == EntityType.HUSK
            || e == EntityType.ILLUSIONER
            || e == EntityType.MAGMA_CUBE
            || e == EntityType.PHANTOM
            || e == EntityType.PIGLIN
            || e == EntityType.PIGLIN_BRUTE
            || e == EntityType.RAVAGER
            || e == EntityType.SHULKER
            || e == EntityType.SILVERFISH
            || e == EntityType.SKELETON
            || e == EntityType.SKELETON_HORSE
            || e == EntityType.SLIME
            || e == EntityType.SPIDER
            || e == EntityType.STRAY
            || e == EntityType.VEX
            || e == EntityType.VINDICATOR
            || e == EntityType.WARDEN
            || e == EntityType.WITCH
            || e == EntityType.WITHER_SKELETON
            || e == EntityType.ZOGLIN
            || e == EntityType.ZOMBIE
            || e == EntityType.ZOMBIE_HORSE
            || e == EntityType.ZOMBIE_VILLAGER
            || e == EntityType.ZOMBIFIED_PIGLIN;
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