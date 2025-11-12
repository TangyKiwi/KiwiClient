package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;

import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class VClip extends Command {
    public VClip() {
        super("vclip", "Clips you through blocks vertically");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("blocks", DoubleArgumentType.doubleArg()).executes(context -> {
            double blocks = context.getArgument("blocks", Double.class);

            int packets = (int) Math.ceil(Math.abs(blocks / 10));

            if (packets > 20) {
                packets = 1;
            }
            
            if (mc.player.hasVehicle()) {
                for (int i = 0; i < packets - 1; i++) {
                    mc.player.networkHandler.sendPacket(VehicleMoveC2SPacket.fromVehicle(mc.player.getVehicle()));
                }
                mc.player.getVehicle().setPosition(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ());
                mc.player.networkHandler.sendPacket(VehicleMoveC2SPacket.fromVehicle(mc.player.getVehicle()));
            } else {
                for (int i = 0; i < packets - 1; i++) {
                    mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), true, mc.player.horizontalCollision));
                }
                mc.player.setPosition(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ());
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + blocks, mc.player.getZ(), true, mc.player.horizontalCollision));
            }
            addMessage("Vclipped §a" + Math.abs(blocks) + "§r blocks " + (blocks >= 0 ? "up" : "down"));

            return SINGLE_SUCCESS;
        }));
    }
}