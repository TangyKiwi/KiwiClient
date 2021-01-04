package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Module;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fly extends Module {

    public long time;

    public Fly() {
        super("Fly", "Fly like in Creative", GLFW.GLFW_KEY_Z, Category.MOVEMENT,
            new SliderSetting("Speed", 0, 5, 1, 1));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        time = System.currentTimeMillis();
    }

    @Subscribe
    public void onTick(TickEvent e) {
        float speed = (float) getSetting(0).asSlider().getValue();

        /** Vanilla fly */
        mc.player.abilities.setFlySpeed(speed / 10);
        mc.player.abilities.allowFlying = true;
        mc.player.abilities.flying = true;


        /** Packet Antikick
        if(mc.player.age % 20 == 0) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY() - 0.06, mc.player.getZ(), false));
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getZ() + 0.06, mc.player.getZ(), true));
        }
         */

        /** Vanilla Antikick
        List<Block> NONSOLID_BLOCKS = Arrays.asList(
                Blocks.AIR, Blocks.LAVA, Blocks.WATER, Blocks.GRASS,
                Blocks.VINE, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS,
                Blocks.SNOW, Blocks.TALL_GRASS, Blocks.FIRE, Blocks.VOID_AIR);
        if(NONSOLID_BLOCKS.contains(mc.world.getBlockState(new BlockPos(mc.player.getX(), mc.player.getY() - 0.069, mc.player.getZ())).getBlock())) {
            mc.player.setVelocity(0, mc.player.age % 20 == 0 ? -0.069 : 0, 0);
        }

        Vec3d forward = new Vec3d(0, 0, speed).rotateY(-(float) Math.toRadians(mc.player.yaw));
        Vec3d strafe = forward.rotateY((float) Math.toRadians(90));

        if (mc.options.keyJump.isPressed()) mc.player.setVelocity(mc.player.getVelocity().add(0, speed, 0));
        if (mc.options.keySneak.isPressed()) mc.player.setVelocity(mc.player.getVelocity().add(0, -speed, 0));
        if (mc.options.keyBack.isPressed())
            mc.player.setVelocity(mc.player.getVelocity().add(-forward.x, 0, -forward.z));
        if (mc.options.keyForward.isPressed())
            mc.player.setVelocity(mc.player.getVelocity().add(forward.x, 0, forward.z));
        if (mc.options.keyLeft.isPressed())
            mc.player.setVelocity(mc.player.getVelocity().add(strafe.x, 0, strafe.z));
        if (mc.options.keyRight.isPressed())
            mc.player.setVelocity(mc.player.getVelocity().add(-strafe.x, 0, -strafe.z)); */
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.abilities.setFlySpeed(0.1f);
        if (!mc.player.abilities.creativeMode) mc.player.abilities.allowFlying = false;
        mc.player.abilities.flying = false;
    }

}
