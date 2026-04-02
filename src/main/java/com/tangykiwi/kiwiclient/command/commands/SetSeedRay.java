package com.tangykiwi.kiwiclient.command.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.module.render.SeedRay;

import net.minecraft.commands.SharedSuggestionProvider;

public class SetSeedRay extends Command{
    public SetSeedRay() {
        super("seedray", "Sets the seed for seedray");
    }

    @Override
    public void build(LiteralArgumentBuilder<SharedSuggestionProvider> builder) {
        builder.then(argument("seed", StringArgumentType.string()).executes(context -> {
            long seed;
            try {
                seed = Long.parseLong(context.getArgument("seed", String.class));
            } catch (Exception e) {
                seed = context.getArgument("seed", String.class).hashCode();
            }
            addMessage("Set SeedRay seed to §a" + seed);
            ((SeedRay) KiwiClient.moduleManager.getModule(SeedRay.class)).worldSeed = seed;

            return SINGLE_SUCCESS;
        }));
    }
}
