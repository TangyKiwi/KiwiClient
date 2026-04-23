package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.world.level.GameType;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class Gamemode extends Command {
    public Gamemode() {
        super("gamemode", "Changes your clientside gamemode", "gm");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        for (GameType gameMode : GameType.values()) {
            builder.then(literal(gameMode.name()).executes(context -> {
                mc.gameMode.setLocalMode(gameMode);
                addMessage("Set clientside gamemode to §d" + gameMode.name().toUpperCase());

                return SINGLE_SUCCESS;
            }));
        }
    }
}
