package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SetTarget extends Command {
    public String target = "";

    public SetTarget() {
        super("target", "Sets a target by name");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("name", StringArgumentType.string()).executes(context -> {
            String targetname = "";
            try {
                targetname = context.getArgument("name", String.class);
            } catch (Exception e) {
                addMessage("Invalid");
                return SINGLE_SUCCESS;
            }
            addMessage("Set target to §a" + targetname);
            target = targetname;

            return SINGLE_SUCCESS;
        }));
    }

    public String getTarget() {
        return target;
    }
}
