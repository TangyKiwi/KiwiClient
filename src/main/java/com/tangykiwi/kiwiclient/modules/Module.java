package com.tangykiwi.kiwiclient.modules;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.settings.BindSetting;
import com.tangykiwi.kiwiclient.modules.settings.Setting;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module {

    public final static int KEY_UNBOUND = -2;
    public final MinecraftClient mc = MinecraftClient.getInstance();
    private String name;
    private Category category;
    private String description;
    private int keyCode;
    private boolean enabled = false;
    private List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, int keyCode, Category category) {
        this.name = name;
        this.description = description;
        this.keyCode = keyCode;
        this.category = category;
        this.settings.add(new BindSetting(this));
    }

    public Module(String name, String description, int keyCode, Category category, Setting<?>... s) {
        this.name = name;
        this.description = description;
        this.keyCode = keyCode;
        this.category = category;
        this.settings = new ArrayList<>(Arrays.asList(s));
        this.settings.add(new BindSetting(this));
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

    public void enable() {
        enabled = true;
        onEnable();
    }

    public void disable() {
        enabled = false;
        onDisable();
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

    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    public Setting getSetting(int setting) {
        return settings.get(setting);
    }
}
