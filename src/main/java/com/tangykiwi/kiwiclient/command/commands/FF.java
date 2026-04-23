package com.tangykiwi.kiwiclient.command.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;

public class FF extends Command {
    public FF() {
        super("ff", "FF, gg go next.");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.executes(context -> {
            String message = "I'm gonna FF, gg go next.";
            mc.getConnection().sendChat(message);
            mc.getConnection().handleDisconnect(new ClientboundDisconnectPacket(Component.literal("Literally just FFed.")));
            return SINGLE_SUCCESS;
        });
    }
}
