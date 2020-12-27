package com.tangykiwi.kiwiclient.modules;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import com.tangykiwi.kiwiclient.modules.movement.*;
import com.tangykiwi.kiwiclient.modules.render.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ModuleManager {

    public static ArrayList<Module> moduleList = new ArrayList<Module>();
    public static MinecraftClient mc = MinecraftClient.getInstance();

    public void init() {
        moduleList.add(new Fly());
        moduleList.add(new Speed());
        moduleList.add(new FullBright());
        moduleList.add(new HUD());
    }

    public ArrayList<Module> getEnabledMods() {
        ArrayList<Module> enabledMods = new ArrayList<Module>();

        for(Module m : moduleList) {
            if(m.isEnabled()) enabledMods.add(m);
        }

        Collections.sort(enabledMods, new ModuleComparator());
        return enabledMods;
    }

    public static class ModuleComparator implements Comparator<Module> {
        @Override
        public int compare(Module a, Module b) {
            if(mc.textRenderer.getWidth(a.getName()) >
                    mc.textRenderer.getWidth(b.getName()))
                return -1;
            else if(mc.textRenderer.getWidth(a.getName()) <
                    mc.textRenderer.getWidth(b.getName()))
                return 1;
            return 0;
        }
    }

    @Subscribe
    public static void handleKeyPress(KeyPressEvent e) {
        if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_F3))
            return;

        for(Module m : moduleList) {
            if(m.getKeyCode() == e.getKeyCode()) m.toggle();
        }
    }
}
