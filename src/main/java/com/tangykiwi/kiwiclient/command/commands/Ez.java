package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Ez extends Command {
    public Ez() {
        super("ez", "Says a big EZ in chat");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Utils.mc.player.sendChatMessage("███████╗███████╗");
            Utils.mc.player.sendChatMessage("██╔════╝╚════██║");
            Utils.mc.player.sendChatMessage("█████╗░░░░███╔═╝");
            Utils.mc.player.sendChatMessage("██╔══╝░░██╔══╝░░");
            Utils.mc.player.sendChatMessage("███████╗███████╗");
            Utils.mc.player.sendChatMessage("╚══════╝╚══════╝");
            return SINGLE_SUCCESS;
        });
    }
}
