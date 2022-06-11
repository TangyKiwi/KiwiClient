package com.tangykiwi.kiwiclient.command.commands;

import com.tangykiwi.kiwiclient.command.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.mixin.ClientPlayerEntityAccessor;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.network.message.MessageSignature;
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
            MessageSignature messageSignature = ((ClientPlayerEntityAccessor) Utils.mc.player)._signChatMessage(ChatMessageSigner.create(Utils.mc.player.getUuid()), Text.literal(message));
            Utils.mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(message, messageSignature, false));
            Utils.mc.player.networkHandler.getConnection().disconnect(Text.literal("Literally just FFed."));
            return SINGLE_SUCCESS;
        });
    }
}