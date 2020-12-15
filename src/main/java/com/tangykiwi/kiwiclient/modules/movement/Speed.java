package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Module;

import com.tangykiwi.kiwiclient.modules.Category;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {

    public Speed() {
        super("Speed", "Go fast", GLFW.GLFW_KEY_GRAVE_ACCENT, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        mc.player.abilities.setWalkSpeed(0.5f);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.abilities.setWalkSpeed(0.1f);
    }

}
