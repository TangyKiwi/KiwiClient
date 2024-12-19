package com.tangykiwi.kiwiclient.module;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class ModuleManager {
    public ArrayList<Module> moduleList = new ArrayList<Module>();

    public void init() {

    }

    public ArrayList<Module> getEnabledMods() {
        ArrayList<Module> enabledMods = new ArrayList<Module>();

        for (Module m : moduleList) {
            if (m.isEnabled()) {
                enabledMods.add(m);
            }
        }

        // add module comparator logic for font renderer

        return enabledMods;
    }

    public Module getModule(Class<? extends Module> c) {
        for (Module m : moduleList) {
            if (m.getClass().equals(c)) {
                return m;
            }
        }

        return null;
    }

    public Module getModule(String name) {
        for (Module m : moduleList) {
            if (m.getName().equals(name)) {
                return m;
            }
        }

        return null;
    }

    public ArrayList<Module> getModulesInCat(Category cat) {
        ArrayList<Module> modulesInCat = new ArrayList<Module>();
        for(Module m : moduleList) {
            if(m.getCategory().equals(cat)) modulesInCat.add(m);
        }
        return modulesInCat;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleKeyPress(KeyPressEvent e) {
        if(mc.currentScreen != null) return;

        // figure out handling for F keys and command prefix
//        if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_F3)) return;
//
//        if(InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_COMMA)) {
//            mc.setScreen(new ChatScreen(""));
//            return;
//        }

        for(Module m : moduleList) {
            if(e.getAction() == 1 && m.getKeyCode() == e.getKeyCode()) m.toggle();
        }
    }
}
