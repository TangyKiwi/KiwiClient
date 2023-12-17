package com.tangykiwi.kiwiclient.gui.mainmenu.dummy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.session.telemetry.TelemetrySender;
import net.minecraft.client.session.telemetry.WorldSession;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.VanillaResourcePackProvider;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.path.AllowedSymlinkPathMatcher;
import net.minecraft.util.path.SymlinkFinder;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

public class DummyClientPlayNetworkHandler extends ClientPlayNetworkHandler {

    private static DummyClientPlayNetworkHandler instance;
    private CombinedDynamicRegistries<ClientDynamicRegistryType> combinedDynamicRegistries = ClientDynamicRegistryType.createCombinedDynamicRegistries();

    public static DummyClientPlayNetworkHandler getInstance() {
        if (instance == null) instance = new DummyClientPlayNetworkHandler();
        return instance;
    }

    private DummyClientPlayNetworkHandler() {
        super(
                MinecraftClient.getInstance(),
                new ClientConnection(NetworkSide.CLIENTBOUND),
                new ClientConnectionState(
                        MinecraftClient.getInstance().getGameProfile(),
                        new WorldSession(TelemetrySender.NOOP, true, Duration.ZERO, "dummy"),
                        ClientDynamicRegistryType.createCombinedDynamicRegistries().getCombinedRegistryManager(),
                        FeatureSet.empty(),
                        "",
                        new ServerInfo("", "", ServerInfo.ServerType.OTHER),
                        null
                )
        );
        LifecycledResourceManagerImpl manager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, List.of());

        combinedDynamicRegistries = combinedDynamicRegistries.with(ClientDynamicRegistryType.REMOTE,
            RegistryLoader.load(manager, combinedDynamicRegistries.getCombinedRegistryManager(), Stream.concat(
                RegistryLoader.DYNAMIC_REGISTRIES.stream(),
                RegistryLoader.DIMENSION_REGISTRIES.stream()
            ).toList())
        );
    }

    @Override
    public DynamicRegistryManager.Immutable getRegistryManager() {
        return combinedDynamicRegistries.getCombinedRegistryManager();
    }
}
