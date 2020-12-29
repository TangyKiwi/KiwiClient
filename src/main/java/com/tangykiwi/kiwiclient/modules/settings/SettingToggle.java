package com.tangykiwi.kiwiclient.modules.settings;

import com.tangykiwi.kiwiclient.util.ColorUtil;


public class SettingToggle extends Settings {

    private String name;
    private String description;
    private String value;
    public Boolean state;
    public int color;

    public SettingToggle(String name, Boolean state) {
        this.name = name;
        this.state = state;
    }

    public SettingToggle withDesc(String description) {
        this.description = description;
        return this;
    }

    public SettingToggle withValue(String value) {
        this.value = value;
        return this;
    }

    public SettingToggle withRange(int best, int good, int mid, int bad, int worst, Boolean reverse) {
        color = ColorUtil.getColorString(Integer.parseInt(value), best, good, mid, bad, worst, reverse);
        return this;
    }

    public void toggle() {
        state = !state;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.description;
    }
    
}
