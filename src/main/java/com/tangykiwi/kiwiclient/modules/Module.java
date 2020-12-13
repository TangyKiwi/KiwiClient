package com.tangykiwi.kiwiclient.modules;

import net.minecraft.client.MinecraftClient;

public class Module {

    private String name;
    private Category category;
    private String description;
    private int keyCode;
    private boolean toggled;

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
        return toggled;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void toggle() {
        toggled = !toggled;
        if(toggled) onEnable();
        else onDisable();
    }

    public void onEnable() {

    }

    public void onDisable() {

    }
}
