package com.tangykiwi.kiwiclient.modules.movement;

import com.tangykiwi.kiwiclient.modules.Module;

import com.tangykiwi.kiwiclient.modules.Category;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module {

    public Fly() {
        super("Fly", "Fly like in Creative", GLFW.GLFW_KEY_Z, Category.MOVEMENT);
    }

    public void onEnable() {
        mc.player.abilities.flying = true;
        mc.player.abilities.allowFlying = true;
    }

    public void onDisable() {
        mc.player.abilities.flying = false;
        mc.player.abilities.allowFlying = false;
    }

}
