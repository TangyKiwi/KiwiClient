package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class VClip extends Command {
    public VClip() {
        super("vclip", "Clips you through blocks vertically");
    }

    @Override
    public void build(LiteralArgumentBuilder<ClientSuggestionProvider> builder) {
        builder.then(argument("blocks", DoubleArgumentType.doubleArg()).executes(context -> {
            double blocks = context.getArgument("blocks", Double.class);

            int packets = (int) Math.ceil(Math.abs(blocks / 10));

            if (packets > 20) {
                packets = 1;
            }
            
            if (mc.player.isPassenger()) {
                for (int i = 0; i < packets - 1; i++) {
                    mc.player.connection.send(ServerboundMoveVehiclePacket.fromEntity(mc.player.getVehicle()));
                }
                mc.player.getVehicle().setPos(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ());
                mc.player.connection.send(ServerboundMoveVehiclePacket.fromEntity(mc.player.getVehicle()));
            } else {
                for (int i = 0; i < packets - 1; i++) {
                    mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, mc.player.horizontalCollision));
                }
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ(), true, mc.player.horizontalCollision));
                mc.player.setPos(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ());
            }
            addMessage("Vclipped §a" + Math.abs(blocks) + "§r blocks " + (blocks >= 0 ? "up" : "down"));

            return SINGLE_SUCCESS;
        }));
    }
}