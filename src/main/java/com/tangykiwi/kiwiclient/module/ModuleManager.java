package com.tangykiwi.kiwiclient.module;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import com.tangykiwi.kiwiclient.module.client.*;
import com.tangykiwi.kiwiclient.module.combat.TriggerBot;
import com.tangykiwi.kiwiclient.module.movement.Fly;
import com.tangykiwi.kiwiclient.module.movement.InvMove;
import com.tangykiwi.kiwiclient.module.movement.NoFall;
import com.tangykiwi.kiwiclient.module.movement.Speed;
import com.tangykiwi.kiwiclient.module.other.DummyModule;
import com.tangykiwi.kiwiclient.module.player.AntiHunger;
import com.tangykiwi.kiwiclient.module.render.Freecam;
import com.tangykiwi.kiwiclient.module.render.FullBright;
import com.tangykiwi.kiwiclient.module.render.NoRender;
import com.tangykiwi.kiwiclient.module.render.SeedRay;
import com.tangykiwi.kiwiclient.module.render.XRay;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.util.InputUtil;
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

        // combat
        moduleList.add(new TriggerBot());

        // movement
        moduleList.add(new Fly());
        moduleList.add(new InvMove());
        moduleList.add(new NoFall());
        moduleList.add(new Speed());

        // other
        moduleList.add(new DummyModule());

        // player
        moduleList.add(new AntiHunger());

        // render
        moduleList.add(new Freecam());
        moduleList.add(new FullBright());
        moduleList.add(new NoRender());
        moduleList.add(new SeedRay());
        moduleList.add(new XRay());
    }

    public ArrayList<Module> getEnabledMods(FontRenderer fontRenderer) {
        ArrayList<Module> enabledMods = new ArrayList<Module>();

        for (Module m : moduleList) {
            if (m.isEnabled()) {
                enabledMods.add(m);
            }
        }

        Collections.sort(enabledMods, new ModuleComparator(fontRenderer));

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
        if(mc.currentScreen != null) return;

        // figure out handling for F keys and command prefix
        if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_F3)) return;
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
