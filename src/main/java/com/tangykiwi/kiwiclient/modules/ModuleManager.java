package com.tangykiwi.kiwiclient.modules;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.GameJoinEvent;
import com.tangykiwi.kiwiclient.event.KeyPressEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.gui.BindScreen;
import com.tangykiwi.kiwiclient.gui.clickgui.ClickGuiScreen;
import com.tangykiwi.kiwiclient.modules.client.*;
import com.tangykiwi.kiwiclient.modules.combat.Criticals;
import com.tangykiwi.kiwiclient.modules.combat.TargetHUD;
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
import net.minecraft.entity.passive.AnimalEntity;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ModuleManager {

    public ArrayList<Module> moduleList = new ArrayList<Module>();
    public static MinecraftClient mc = MinecraftClient.getInstance();
    public Module module = null;

    public void init() {
        //client
        moduleList.add(new ActiveMods());
        moduleList.add(new BetterTab());
        moduleList.add(new BetterChat());
        moduleList.add(new ClickGui());
        moduleList.add(new Compass(325, 325, 1, 2, true));
        moduleList.add(new HUD());
        moduleList.add(new InventoryViewer());
        moduleList.add(new MountHUD());
        moduleList.add(new NoScoreboard());
        moduleList.add(new PotionTimers());
        moduleList.add(new Time());
        moduleList.add(new Tooltips());
        moduleList.add(new VanillaSpoof());

        //combat
        moduleList.add(new Criticals());
        moduleList.add(new TargetHUD());
        moduleList.add(new TriggerBot());

        //movement
        moduleList.add(new BoatPhase());
        moduleList.add(new ElytraFly());
        moduleList.add(new EntityFly());
        moduleList.add(new FastBridge());
        moduleList.add(new Fly());
        moduleList.add(new InvMove());
        moduleList.add(new NoFall());
        moduleList.add(new NoWorldBorder());
        moduleList.add(new SafeWalk());
        moduleList.add(new Speed());

        //other
        moduleList.add(new AntiHuman());
        moduleList.add(new Background());
        moduleList.add(new Cape());
        moduleList.add(new Deadmau5Ears());
        moduleList.add(new LoadingScreen());
        moduleList.add(new MainMenu());
        moduleList.add(new NoIP());
        moduleList.add(new NoLO());

        //player
        moduleList.add(new AntiBlind());
        moduleList.add(new AntiHunger());
        moduleList.add(new ArmorSwap());
        moduleList.add(new AutoContainer());
        moduleList.add(new AutoTool());

        //render
        moduleList.add(new ESP());
        moduleList.add(new Freecam());
        moduleList.add(new FullBright());
        moduleList.add(new ItemPhysics());
        moduleList.add(new Nametags());
        moduleList.add(new NoPortal());
        moduleList.add(new NoRender());
        moduleList.add(new Search());
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
        @Override
        public int compare(Module a, Module b) {
            int aWidth = IFont.CONSOLAS.getStringWidth(a.getName());
            int bWidth = IFont.CONSOLAS.getStringWidth(b.getName());
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

        if (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_F3)) return;

        if(InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_COMMA)) {
            mc.setScreen(new ChatScreen(""));
            return;
        }

        for(Module m : moduleList) {
            if(e.getAction() == 1 && m.getKeyCode() == e.getKeyCode()) m.toggle();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent e) {
        if(module != null) {
            mc.setScreen(new BindScreen(module));
            module = null;
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onGameJoin(GameJoinEvent e) {
        for(Module m : getEnabledMods()) {
            m.onEnable();
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
