package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.loader.impl.lib.mappingio.format.FeatureSet;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.LastSeenMessagesTracker;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.world.flag.FeatureFlagSet;

@Mixin(ClientPacketListener.class)
public interface ClientPacketListenerAccessor {
    @Accessor("serverChunkRadius")
    int kiwiclient$getServerChunkRadius();

    @Accessor("signedMessageEncoder")
    SignedMessageChain.Encoder kiwiclient$signedMessageEncoder();

    @Accessor("lastSeenMessages")
    LastSeenMessagesTracker kiwiclient$lastSeenMessages();

    @Accessor("registryAccess")
    RegistryAccess.Frozen kiwiclient$registryAccess();

    @Accessor("enabledFeatures")
    FeatureFlagSet kiwiclient$getEnabledFeatures();

    @Accessor("COMMAND_NODE_BUILDER")
    static ClientboundCommandsPacket.NodeBuilder<ClientSuggestionProvider> kiwiclient$getCommandNodeFactory() {
        return null;
    }
}
