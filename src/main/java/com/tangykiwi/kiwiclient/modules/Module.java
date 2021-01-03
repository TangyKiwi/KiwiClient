package com.tangykiwi.kiwiclient.modules;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Method;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.tangykiwi.kiwiclient.modules.settings.Settings;

public class Module {

    public final static int KEY_UNBOUND = -2;
    public final MinecraftClient mc = MinecraftClient.getInstance();
    private String name;
    private Category category;
    private String description;
    private int keyCode;
    private boolean enabled = false;
    private List<Settings> settings = new ArrayList<>();

    public Module(String name, String description, int keyCode, Category category) {
        this.name = name;
        this.description = description;
        this.keyCode = keyCode;
        this.category = category;
    }

    public Module(String name, String description, int keyCode, Category category, Settings... s) {
        // new Module(name, description, keyCode, category);
        this.name = name;
        this.description = description;
        this.keyCode = keyCode;
        this.category = category;
        settings = new ArrayList<>(Arrays.asList(s));
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public Category getCategory() { return category; }

    public void toggle() {
        enabled = !enabled;
        if(enabled) onEnable();
        else onDisable();
    }

    public void onEnable() {
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                KiwiClient.eventBus.register(this);
                break;
            }
        }
    }

    public void onDisable() {
        try {
            for (Method method : getClass().getMethods()) {
                if (method.isAnnotationPresent(Subscribe.class)) {
                    KiwiClient.eventBus.unregister(this);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setKeyCode(int key) {
        keyCode = key;
    }

    public void setToggled(boolean toggle) {enabled = toggle;}

    public List<Settings> getSettings() {
        return this.settings;
    }

    public List<Settings> getToggledSettings() {
        List<Settings> toggledSettings = new ArrayList<Settings>();
        for(Settings s : settings) {
            if(s.asToggle().state) {
                toggledSettings.add(s);
            }
        }

        return toggledSettings;
    }

    public Settings getSetting(int setting) {
        return settings.get(setting);
    }
}
