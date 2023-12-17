package com.tangykiwi.kiwiclient.gui.mainmenu.dummy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.RegistryKeys;;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

public class DummyClientWorld extends ClientWorld {

    private static DummyClientWorld instance;

    public static DummyClientWorld getInstance() {
        if (instance == null) instance = new DummyClientWorld(DummyClientPlayNetworkHandler.getInstance());
        return instance;
    }

    private DummyClientWorld(DummyClientPlayNetworkHandler dummyClientPlayNetworkHandler) {
        super(
                dummyClientPlayNetworkHandler,
                new Properties(Difficulty.EASY, false, true),
                World.OVERWORLD,
                dummyClientPlayNetworkHandler.getRegistryManager().get(RegistryKeys.DIMENSION_TYPE).entryOf(DimensionTypes.OVERWORLD),
                0,
                0,
                MinecraftClient.getInstance()::getProfiler,
                MinecraftClient.getInstance().worldRenderer,
                false,
                0
        );
    }
}