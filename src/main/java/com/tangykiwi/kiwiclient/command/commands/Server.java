package com.tangykiwi.kiwiclient.command.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;

import net.minecraft.command.CommandSource;

public class Server extends Command {
    public Server() {
        super("server", "Displays information about the server");
    }
    
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            return SINGLE_SUCCESS;
        });
    }
}
