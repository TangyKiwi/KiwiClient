package com.tangykiwi.kiwiclient.module.setting;

import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;

import net.minecraft.client.gui.DrawContext;

public abstract class Setting<T> {
    private String name;
    private String desc;
    private T value;

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

    public abstract int render(DrawContext context, CategoryWindow window, int curYoffset);
}
