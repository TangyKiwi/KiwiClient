package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class VClip extends Command {
    public VClip() {
        super("vclip", "Lets you clip through blocks vertically.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("blocks", DoubleArgumentType.doubleArg()).executes(context -> {
            ClientPlayerEntity player = Utils.mc.player;
            assert player != null;

            double blocks = context.getArgument("blocks", Double.class);
            if (player.hasVehicle()) {
                Entity vehicle = player.getVehicle();
                vehicle.setPosition(vehicle.getX(), vehicle.getY() + blocks, vehicle.getZ());
            }
            player.setPosition(player.getX(), player.getY() + blocks, player.getZ());
            addMessage("Vclipped §a" + Math.abs(blocks) + "§r blocks " + (blocks >= 0 ? "up" : "down"));

            return SINGLE_SUCCESS;
        }));
    }
}
