package com.tangykiwi.kiwiclient.gui.mainmenu.dummy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.OptionalLong;

public class DummyClientWorld extends ClientWorld {

    private static DummyClientWorld instance;

    public static DummyClientWorld getInstance() {
        if (instance == null) instance = new DummyClientWorld();
        return instance;
    }

    private DummyClientWorld() {
        super(DummyClientPlayNetworkHandler.getInstance(), new Properties(Difficulty.EASY, false, true), World.OVERWORLD, RegistryEntry.of(new DimensionType(OptionalLong.empty(), false, false, false, false, 1.0, false, true, 16, 16, 16, BlockTags.INFINIBURN_OVERWORLD, DimensionTypes.OVERWORLD_ID, 0.0F, new DimensionType.MonsterSettings(true, false, ConstantIntProvider.ZERO, 0))), 0,0, MinecraftClient.getInstance()::getProfiler, MinecraftClient.getInstance().worldRenderer, false, 0);
    }
}
