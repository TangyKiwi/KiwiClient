package com.tangykiwi.kiwiclient.gui.mainmenu.dummy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.*;

import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

public class DummyClientWorld extends ClientWorld {

    private static DummyClientWorld instance;

    public static DummyClientWorld getInstance() {
        if (instance == null) instance = new DummyClientWorld();
        return instance;
    }

    private DummyClientWorld() {
        super(DummyClientPlayNetworkHandler.getInstance(), new Properties(Difficulty.EASY, false, true), World.OVERWORLD, MinecraftClient.getInstance().world.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).getEntry(MinecraftClient.getInstance().world.getDimension()), 0,0, MinecraftClient.getInstance()::getProfiler, MinecraftClient.getInstance().worldRenderer, false, 0);
    }
}
