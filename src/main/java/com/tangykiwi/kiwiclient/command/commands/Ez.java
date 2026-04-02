package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;

import net.minecraft.commands.SharedSuggestionProvider;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class Ez extends Command {
    public Ez() {
        super("ez", "Says a big EZ in chat");
    }

    String[] messages = {
            "███████╗███████╗",
            "███████╗███████╗",
            "█████╗░░░░███╔═╝",
            "██╔══╝░░██╔══╝░░",
            "███████╗███████╗",
            "╚══════╝╚══════╝"};

    @Override
    public void build(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.executes(context -> {
            for(String message : messages) {
                mc.getConnection().sendChat(message);
            }
            return SINGLE_SUCCESS;
        });
    }
}
