package com.tangykiwi.kiwiclient.gui.mainmenu.dummy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;

import java.time.Duration;

public class DummyClientPlayNetworkHandler extends ClientPlayNetworkHandler {

    private static DummyClientPlayNetworkHandler instance;

    public static DummyClientPlayNetworkHandler getInstance() {
        if (instance == null) instance = new DummyClientPlayNetworkHandler();
        return instance;
    }

    public static void newInstance() {
        instance = new DummyClientPlayNetworkHandler();
    }

    private DummyClientPlayNetworkHandler() {
        super(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), null, MinecraftClient.getInstance().getSession().getProfile(), new WorldSession(TelemetrySender.NOOP, true, Duration.ZERO));
    }
}
