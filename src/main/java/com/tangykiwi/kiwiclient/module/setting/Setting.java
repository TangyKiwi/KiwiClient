package com.tangykiwi.kiwiclient.module.setting;

public abstract class Setting {
    public String name;
    public String desc;

    public Setting(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
