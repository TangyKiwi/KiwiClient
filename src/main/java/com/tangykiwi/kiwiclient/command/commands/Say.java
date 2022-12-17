package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;

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
                Utils.mc.getNetworkHandler().sendChatMessage(msg);
            }

            return SINGLE_SUCCESS;
        }));
    }
}
