package com.tangykiwi.kiwiclient.module.setting;

public class ToggleSetting extends Setting<Boolean> {
    // value = boolean enabled

    public ToggleSetting(String name, String desc) {
        super(name, desc);
        this.setValue(false);
    }

    public ToggleSetting(String name, String desc, boolean enabled) {
        super(name, desc);
        this.setValue(enabled);
    }
}
