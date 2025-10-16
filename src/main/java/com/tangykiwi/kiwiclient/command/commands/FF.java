package com.tangykiwi.kiwiclient.command.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class FF extends Command {
    public FF() {
        super("ff", "FF, gg go next.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            String message = "I'm gonna FF, gg go next.";
            mc.getNetworkHandler().sendChatMessage(message);
            mc.getNetworkHandler().getConnection().disconnect(Text.literal("Literally just FFed."));
            return SINGLE_SUCCESS;
        });
    }
}
