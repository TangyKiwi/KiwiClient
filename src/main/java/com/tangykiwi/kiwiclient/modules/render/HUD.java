package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;

import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.Category;
import org.lwjgl.glfw.GLFW;
import java.util.List;
import java.util.ArrayList;

public class HUD extends Module {
    
    private double tps = 20;
    private double bps = 0;
    private long lastPacked = 0;
    private long timer = 0;
    private int ping = 0;
    private String ip = "";

    public List<String> mods = new ArrayList<>();

    public HUD() {
        super("HUD", "Shows info as an overlay", GLFW.GLFW_KEY_H, Category.RENDER);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        update();
    }

    public void update() {
    }

}
