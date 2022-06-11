package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.mixin.ClientPlayerEntityAccessor;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Say extends Command {
    public Say() {
        super("say", "Send a message in chat");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            String message = context.getArgument("message", String.class);
            MessageSignature messageSignature = ((ClientPlayerEntityAccessor) Utils.mc.player)._signChatMessage(ChatMessageSigner.create(Utils.mc.player.getUuid()), Text.literal(message));
            Utils.mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket(message, messageSignature, false));

            return SINGLE_SUCCESS;
        }));
    }
}
