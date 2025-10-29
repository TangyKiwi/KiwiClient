package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.world.GameMode;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class Gamemode extends Command {
    public Gamemode() {
        super("gamemode", "Changes your clientside gamemode", "gm");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        for (GameMode gameMode : GameMode.values()) {
            builder.then(literal(gameMode.name()).executes(context -> {
                mc.interactionManager.setGameMode(gameMode);
                addMessage("Set clientside gamemode to §d" + gameMode.name().toUpperCase());

                return SINGLE_SUCCESS;
            }));
        }
    }
}
