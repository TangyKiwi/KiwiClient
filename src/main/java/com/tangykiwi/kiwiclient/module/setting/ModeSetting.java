package com.tangykiwi.kiwiclient.module.setting;

import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;

import net.minecraft.client.gui.DrawContext;

public class ModeSetting extends Setting<Integer> {
    public String[] modes;
    // value = index of current mode

    public ModeSetting(String name, String desc, String[] modes) {
        super(name, desc);
        this.modes = modes;
        this.setValue(0);
    }

    public ModeSetting(String name, String desc, String[] modes, int index) {
        super(name, desc);
        this.modes = modes;
        this.setValue(index);
    }

    @Override
    public int render(DrawContext context, CategoryWindow window, int curYoffset) {
        int fontHeight = (int) window.fontHeight;
        return fontHeight + 1;
    }
}
