package com.tangykiwi.kiwiclient.module;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.InputConstants;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import com.tangykiwi.kiwiclient.module.client.*;
import com.tangykiwi.kiwiclient.module.combat.*;
import com.tangykiwi.kiwiclient.module.movement.*;
import com.tangykiwi.kiwiclient.module.other.*;
import com.tangykiwi.kiwiclient.module.player.*;
import com.tangykiwi.kiwiclient.module.render.*;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class ModuleManager {
    public ArrayList<Module> moduleList = new ArrayList<Module>();

    public void init() {
        // client
        moduleList.add(new ClickGUI());
        moduleList.add(new HUD());
        moduleList.add(new Tooltips());
        moduleList.add(new VanillaSpoof());

        // combat
        moduleList.add(new Criticals());
        moduleList.add(new TriggerBot());

        // movement
        moduleList.add(new EntityFly());
        moduleList.add(new Fly());
        moduleList.add(new InvMove());
        moduleList.add(new NoFall());
        moduleList.add(new Speed());

        // other
        moduleList.add(new Cape());
        moduleList.add(new Deadmau5Ears());
        moduleList.add(new DummyModule());

        // player
        moduleList.add(new AntiHunger());
        moduleList.add(new FastBreak());

        // render
        moduleList.add(new ESP());
        moduleList.add(new Freecam());
        moduleList.add(new FullBright());
        moduleList.add(new Nametags());
        moduleList.add(new NoRender());
        moduleList.add(new SeedRay());
        moduleList.add(new StorageESP());
        moduleList.add(new Tracers());
        moduleList.add(new XRay());
    }

    public ArrayList<Module> getEnabledMods(FontRenderer fontRenderer) {
        ArrayList<Module> enabledMods = new ArrayList<Module>();

        for (Module m : moduleList) {
            if (m.isEnabled()) {
                enabledMods.add(m);
            }
        }

        if (fontRenderer != null) Collections.sort(enabledMods, new ModuleComparator(fontRenderer));

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

    public static class ModuleComparator implements Comparator<Module> {
        public FontRenderer fontRenderer;

        public ModuleComparator() {
            this.fontRenderer = KiwiClient.fontManager.getSize(8, FontManager.Type.CONSOLAS);
        }

        public ModuleComparator(FontRenderer fontRenderer) {
            this.fontRenderer = fontRenderer;
        }

        @Override
        public int compare(Module a, Module b) {
            float aWidth = this.fontRenderer.getStringWidth(a.getName());
            float bWidth = this.fontRenderer.getStringWidth(b.getName());
            if(aWidth > bWidth) return -1;
            else if(aWidth < bWidth) return 1;
            else if(aWidth == bWidth && a.getName().compareTo(b.getName()) < 0) return -1;
            else if(aWidth == bWidth && a.getName().compareTo(b.getName()) > 0) return 1;
            return 0;
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleKeyPress(KeyPressEvent e) {
        if(mc.gui.screen() != null) return;

        // figure out handling for F keys and command prefix
        if (InputConstants.isKeyDown(mc.getWindow(), GLFW.GLFW_KEY_F3)) return;
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
