package com.tangykiwi.kiwiclient.module;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.setting.BindSetting;
import com.tangykiwi.kiwiclient.module.setting.Setting;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class Module {
    public final static int KEY_UNBOUND = -1;

    private String name;
    private Category category;
    private String description;
    private boolean enabled = false;
    private ArrayList<Setting<?>> settings;
    private BindSetting bind;

    public Module(String name, String description, Category category) {
        this(name, description, KEY_UNBOUND, category);
    }

    public Module(String name, String description, Category category, Setting<?>... s) {
        this(name, description, KEY_UNBOUND, category, s);
    }

    public Module(String name, String description, int keyCode, Category category, Setting<?>... s) {
        this.name = name;
        this.description = description;
        this.category = category;

        this.settings = new ArrayList<>(Arrays.asList(s));
        bind = new BindSetting(keyCode);
        this.settings.add(bind);
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getKeyCode() {
        return bind.getValue();
    }

    public void setKeyCode(int key) {
        bind.setValue(key);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
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

    public ArrayList<Setting<?>> getSettings() {
        return settings;
    }

    public Setting<?> getSetting(int setting) {
        return settings.get(setting);
    }

    public Setting<?> getSetting(String name) {
        return settings.stream()
                .filter(setting -> setting.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void addSetting(Setting<?> setting) {
        settings.add(setting);
    }
}
