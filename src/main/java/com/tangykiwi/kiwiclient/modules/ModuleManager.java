package com.tangykiwi.kiwiclient.modules;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import com.tangykiwi.kiwiclient.gui.clickgui.ClickGuiScreen;
import com.tangykiwi.kiwiclient.modules.client.*;
import com.tangykiwi.kiwiclient.modules.combat.Criticals;
import com.tangykiwi.kiwiclient.modules.combat.TriggerBot;
import com.tangykiwi.kiwiclient.modules.movement.*;
import com.tangykiwi.kiwiclient.modules.other.*;
import com.tangykiwi.kiwiclient.modules.player.*;
import com.tangykiwi.kiwiclient.modules.render.*;
import com.tangykiwi.kiwiclient.modules.render.seedray.SeedRay;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ModuleManager {

    public ArrayList<Module> moduleList = new ArrayList<Module>();
    public static MinecraftClient mc = MinecraftClient.getInstance();

    public void init() {
        //client
        moduleList.add(new ActiveMods());
        moduleList.add(new BetterTab());
        moduleList.add(new BetterChat());
        moduleList.add(new ClickGui());
        moduleList.add(new Compass(325, 325, 1, 2, true));
        moduleList.add(new HUD());
        moduleList.add(new MountHUD());
        moduleList.add(new NoScoreboard());
        moduleList.add(new Time());
        moduleList.add(new Tooltips());
        moduleList.add(new VanillaSpoof());

        //combat
        moduleList.add(new Criticals());
        moduleList.add(new TriggerBot());

        //movement
        moduleList.add(new FastBridge());
        moduleList.add(new Fly());
        moduleList.add(new NoClip());
        moduleList.add(new NoFall());
        moduleList.add(new SafeWalk());
        moduleList.add(new Speed());

        //other
        moduleList.add(new Background());
        moduleList.add(new Cape());
        moduleList.add(new Deadmau5Ears());
        moduleList.add(new LoadingScreen());
        moduleList.add(new MainMenu());
        //moduleList.add(new WeaponMaster());

        //player
        moduleList.add(new AntiHunger());
        moduleList.add(new ArmorSwap());
        moduleList.add(new AutoTool());
        moduleList.add(new InventoryViewer());

        //render
        moduleList.add(new ESP());
        moduleList.add(new FullBright());
        moduleList.add(new ItemPhysics());
        moduleList.add(new Nametags());
        moduleList.add(new NoPortal());
        moduleList.add(new NoRender());
        moduleList.add(new SeedRay());
        moduleList.add(new StorageESP());
        moduleList.add(new TNTimer());
        moduleList.add(new Tracers());
        moduleList.add(new XRay());
        moduleList.add(new Zoom());
    }

    public ArrayList<Module> getEnabledMods() {
        ArrayList<Module> enabledMods = new ArrayList<Module>();

        for(Module m : moduleList) {
            if(m.isEnabled()) enabledMods.add(m);
        }

        if(mc.currentScreen instanceof ClickGuiScreen) {
            enabledMods.add(getModule(ClickGui.class));
        }

        Collections.sort(enabledMods, new ModuleComparator());
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

    public Module getModuleByName(String name) {
        for (Module m : moduleList) {
            if (name.equalsIgnoreCase(m.getName()))
                return m;
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
//        @Override
//        public int compare(Module a, Module b) {
//            if(mc.textRenderer.getWidth(a.getName()) >
//                    mc.textRenderer.getWidth(b.getName()))
//                return -1;
//            else if(mc.textRenderer.getWidth(a.getName()) <
//                    mc.textRenderer.getWidth(b.getName()))
//                return 1;
//            return 0;
//        }
        @Override
        public int compare(Module a, Module b) {
            if(IFont.CONSOLAS.getStringWidth(a.getName()) >
                    IFont.CONSOLAS.getStringWidth(b.getName()))
                return -1;
            else if(IFont.CONSOLAS.getStringWidth(a.getName()) <
                    IFont.CONSOLAS.getStringWidth(b.getName()))
                return 1;
            return 0;
        }
    }

    @Subscribe
    public void handleKeyPress(KeyPressEvent e) {
        if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_F3)) return;

        if(InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_COMMA)) {
            mc.setScreen(new ChatScreen(""));
            return;
        }

        for(Module m : moduleList) {
            if(m.getKeyCode() == e.getKeyCode()) m.toggle();
        }
    }

    /**
    @Subscribe
    public static void handleMouseButton(MouseButtonEvent e) {
        for(Module m : moduleList) {
            if(m.getKeyCode() == e.getKey()) m.toggle();
        }
    } */
}
