package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Module;

import com.tangykiwi.kiwiclient.modules.Category;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module {

    public Fly() {
        super("Fly", "Fly like in Creative", GLFW.GLFW_KEY_Z, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        mc.player.abilities.allowFlying = true;
        mc.player.abilities.flying = true;
        mc.player.abilities.setFlySpeed();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (!mc.player.abilities.creativeMode) mc.player.abilities.allowFlying = false;
        mc.player.abilities.flying = false;
    }

}
