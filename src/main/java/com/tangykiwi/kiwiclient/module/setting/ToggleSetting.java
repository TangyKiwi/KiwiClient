package com.tangykiwi.kiwiclient.module.setting;

public class ToggleSetting extends Setting {
    public boolean enabled;

    public ToggleSetting(String name, String desc) {
        super(name, desc);
        this.enabled = false;
    }

    public ToggleSetting(String name, String desc, boolean enabled) {
        super(name, desc);
        this.enabled = enabled;
    }
}
