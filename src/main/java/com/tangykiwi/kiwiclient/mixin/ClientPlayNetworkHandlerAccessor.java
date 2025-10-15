package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetworkHandler.class)
public interface ClientPlayNetworkHandlerAccessor {
    @Accessor("chunkLoadDistance")
    int kiwiclient$getChunkLoadDistance();

    @Accessor("messagePacker")
    MessageChain.Packer kiwiclient$getMessagePacker();

    @Accessor("lastSeenMessagesCollector")
    LastSeenMessagesCollector kiwiclient$getLastSeenMessagesCollector();

    @Accessor("combinedDynamicRegistries")
    DynamicRegistryManager.Immutable kiwiclient$getCombinedDynamicRegistries();

    @Accessor("enabledFeatures")
    FeatureSet kiwiclient$getEnabledFeatures();

    @Accessor("COMMAND_NODE_FACTORY")
    static CommandTreeS2CPacket.NodeFactory<ClientCommandSource> kiwiclient$getCommandNodeFactory() {
        return null;
    }
}
