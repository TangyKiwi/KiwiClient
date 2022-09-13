package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.mixin.ClientPlayerEntityAccessor;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Say extends Command {
    public Say() {
        super("say", "Send a message in chat");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            String msg = context.getArgument("message", String.class);
            
            if (msg != null) {
                MessageMetadata metadata = MessageMetadata.of(Utils.mc.player.getUuid());
                DecoratedContents contents = new DecoratedContents(msg);

                LastSeenMessageList.Acknowledgment acknowledgment = Utils.mc.getNetworkHandler().consumeAcknowledgment();
                MessageSignatureData messageSignatureData = ((ClientPlayerEntityAccessor) Utils.mc.player)._signChatMessage(metadata, contents, acknowledgment.lastSeen());
                Utils.mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(contents.plain(), metadata.timestamp(), metadata.salt(), messageSignatureData, contents.isDecorated(), acknowledgment));

            }

            return SINGLE_SUCCESS;
        }));
    }
}
