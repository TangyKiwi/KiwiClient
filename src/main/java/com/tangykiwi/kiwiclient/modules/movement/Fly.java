package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

public class Fly extends Module {
    public Fly() {
        super("Fly", "Fly like in Creative", KEY_UNBOUND, Category.MOVEMENT,
            new ModeSetting("Antikick", "None", "Packet").withDesc("Antikick mode"),
            new SliderSetting("Speed", 0.1, 5, 1, 1));
    }

    @Subscribe
    public void onTick(TickEvent e) {
        float speed = (float) getSetting(1).asSlider().getValue();

        /** Vanilla fly */
        mc.player.getAbilities().setFlySpeed(speed / 10);
        mc.player.getAbilities().allowFlying = true;
        mc.player.getAbilities().flying = true;

        if(getSetting(0).asMode().mode == 1) {
            if(mc.player.age % 20 == 0) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() - 0.03130D, mc.player.getZ(), false));
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.getAbilities().setFlySpeed(0.1f);
        if (!mc.player.getAbilities().creativeMode) mc.player.getAbilities().allowFlying = false;
        mc.player.getAbilities().flying = false;
    }

}
