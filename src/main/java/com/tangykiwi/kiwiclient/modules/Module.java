package com.tangykiwi.kiwiclient.modules;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Method;

public class Module {

    private String name;
    private Category category;
    private String description;
    private int keyCode;
    private boolean enabled = false;

    public MinecraftClient mc = MinecraftClient.getInstance();

    public Module(String name, String description, int keyCode, Category category) {
        this.name = name;
        this.description = description;
        this.keyCode = keyCode;
        this.category = category;
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
}
