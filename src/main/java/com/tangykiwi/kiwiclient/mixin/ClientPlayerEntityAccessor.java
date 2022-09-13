package com.tangykiwi.kiwiclient.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientPlayerEntity.class)
public interface ClientPlayerEntityAccessor {
    @Invoker("signChatMessage")
    MessageSignatureData _signChatMessage(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages);
}
