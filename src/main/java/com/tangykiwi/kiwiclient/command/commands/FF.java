package com.tangykiwi.kiwiclient.command.commands;

import com.tangykiwi.kiwiclient.command.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.mixin.ClientPlayerEntityAccessor;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.DecoratedContents;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class FF extends Command {

    public FF() {
        super("ff", "FF, gg go next.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            String message = "I'm gonna FF, gg go next.";
            MessageMetadata metadata = MessageMetadata.of(Utils.mc.player.getUuid());
            DecoratedContents contents = new DecoratedContents(message);
            LastSeenMessageList.Acknowledgment acknowledgment = Utils.mc.getNetworkHandler().consumeAcknowledgment();
            MessageSignatureData messageSignatureData = ((ClientPlayerEntityAccessor) Utils.mc.player)._signChatMessage(metadata, contents, acknowledgment.lastSeen());
            Utils.mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(contents.plain(), metadata.timestamp(), metadata.salt(), messageSignatureData, contents.isDecorated(), acknowledgment));
            Utils.mc.player.networkHandler.getConnection().disconnect(Text.literal("Literally just FFed."));
            return SINGLE_SUCCESS;
        });
    }
}