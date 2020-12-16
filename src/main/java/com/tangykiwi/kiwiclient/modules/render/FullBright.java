package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Module;

import com.tangykiwi.kiwiclient.modules.Category;
import org.lwjgl.glfw.GLFW;

public class FullBright extends Module {

    public FullBright() {
        super("FullBright", "Increase gamma", GLFW.GLFW_KEY_C, Category.RENDER);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if(mc.options.gamma < 12) mc.options.gamma += 0.5;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        while(mc.options.gamma > 1) mc.options.gamma -= 0.5;
    }
}
