package com.tangykiwi.kiwiclient.gui.mainmenu.dummy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class DummyClientPlayerEntity extends ClientPlayerEntity {

    private static DummyClientPlayerEntity instance;
    private Identifier skinIdentifier = null;

    public static DummyClientPlayerEntity getInstance() {
        if (instance == null) instance = new DummyClientPlayerEntity();
        return instance;
    }

    public static void newInstance() {
        instance = new DummyClientPlayerEntity();
    }

    private DummyClientPlayerEntity() {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null, false, false);
        skinIdentifier = MinecraftClient.getInstance().getSkinProvider().getSkinTextures(getGameProfile()).texture();
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }
}
