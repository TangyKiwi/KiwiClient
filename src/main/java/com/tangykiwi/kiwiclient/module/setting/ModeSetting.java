package com.tangykiwi.kiwiclient.module.setting;

import java.util.ArrayList;
import java.util.List;

public class ModeSetting extends Setting {
    public String[] modes;
    public int index;

    public ModeSetting(String name, String desc, String[] modes) {
        super(name, desc);
        this.modes = modes;
        this.index = 0;
    }

    public ModeSetting(String name, String desc, String[] modes, int index) {
        super(name, desc);
        this.modes = modes;
        this.index = index;
    }
}
