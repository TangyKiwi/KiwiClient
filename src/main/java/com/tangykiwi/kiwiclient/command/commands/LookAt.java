package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.tangykiwi.kiwiclient.util.Utils.mc;

public class LookAt extends Command {
    public LookAt() {
        super("lookat", "Forces player to look at a position");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder
            .then(argument("x", IntegerArgumentType.integer())
            .then(argument("y", IntegerArgumentType.integer())
            .then(argument("z", IntegerArgumentType.integer())
                .executes(context -> {
                    lookAt(new Vec3d(context.getArgument("x", Integer.class), context.getArgument("y", Integer.class), context.getArgument("z", Integer.class)));

                    return SINGLE_SUCCESS;
                }
        ))));
    }

    private void lookAt(Vec3d pos) {
        Vec3d player = mc.player.getEyePos();
        double dirx = player.getX() - pos.x;
        double diry = player.getY() - pos.y;
        double dirz = player.getZ() - pos.z;
        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0 / Math.PI;
        yaw = yaw * 180.0 / Math.PI;
        yaw += 90f;

        mc.player.setPitch((float) pitch);
        mc.player.setYaw((float) yaw);
        addMessage("Forced player to look at §b[§a" + pos.x + ", " + pos.y + ", " + pos.z + "§b] §rwith pitch §a" + pitch + " §rand yaw §a" + yaw);
        //mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(player.getX(), player.getY(), player.getZ(), (float) yaw, (float) pitch, player.isOnGround()));
    }
}
