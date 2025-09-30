package com.tangykiwi.kiwiclient.module.setting;

import com.google.gson.JsonElement;
import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;

import net.minecraft.client.gui.DrawContext;

public abstract class Setting<T> {
    private String name;
    private String desc;
    private T value;
    public int height;

    public Setting(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int getHeight() {
        return height;
    }

    public abstract void render(DrawContext context, CategoryWindow window, int curYoffset);

    public abstract void read(JsonElement je);

    public abstract JsonElement write();

    public ModeSetting asMode() {
        return (ModeSetting) this;
    }

    public ToggleSetting asToggle() {
        return (ToggleSetting) this;
    }

    public BindSetting asBind() {
        return (BindSetting) this;
    }
}
