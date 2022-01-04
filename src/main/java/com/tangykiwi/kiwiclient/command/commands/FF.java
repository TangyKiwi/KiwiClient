package com.tangykiwi.kiwiclient.command.commands;

import com.tangykiwi.kiwiclient.command.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class FF extends Command {

    public FF() {
        super("ff", "FF, gg go next.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Utils.mc.getNetworkHandler().sendPacket(new ChatMessageC2SPacket("I'm gonna FF, gg go next."));
            Utils.mc.player.networkHandler.getConnection().disconnect(new LiteralText("Literally just FFed."));
            return SINGLE_SUCCESS;
        });
    }
}