package com.tangykiwi.kiwiclient.gui.mainmenu.dummy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientDynamicRegistryType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.minecraft.client.util.telemetry.WorldSession;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.resource.LifecycledResourceManagerImpl;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.VanillaDataPackProvider;

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

    public static void newInstance() {
        instance = new DummyClientPlayNetworkHandler();
    }

    private DummyClientPlayNetworkHandler() {
        super(MinecraftClient.getInstance(), null, new ClientConnection(NetworkSide.CLIENTBOUND), null, MinecraftClient.getInstance().getSession().getProfile(), new WorldSession(TelemetrySender.NOOP, true, Duration.ZERO));
        LifecycledResourceManagerImpl manager = new LifecycledResourceManagerImpl(ResourceType.SERVER_DATA, List.of(new VanillaDataPackProvider().getResourcePack()));

        combinedDynamicRegistries = combinedDynamicRegistries.with(ClientDynamicRegistryType.REMOTE,
            RegistryLoader.load(manager, combinedDynamicRegistries.getCombinedRegistryManager(), Stream.concat(
                RegistryLoader.DYNAMIC_REGISTRIES.stream(),
                RegistryLoader.DIMENSION_REGISTRIES.stream()
            ).toList())
        );
    }

    @Override
    public DynamicRegistryManager getRegistryManager() {
        return combinedDynamicRegistries.getCombinedRegistryManager();
    }
}
