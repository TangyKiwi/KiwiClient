package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.world.GameMode;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Gamemode extends Command {
    public Gamemode() {
        super("gamemode", "Changes your clientside gamemode", "gm");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        for (GameMode gameMode : GameMode.values()) {
            builder.then(literal(gameMode.getName()).executes(context -> {
                Utils.mc.interactionManager.setGameMode(gameMode);
                addMessage("Set clientside gamemode to §d" + gameMode.getName().toUpperCase());

                return SINGLE_SUCCESS;
            }));
        }
    }
}
