package com.tangykiwi.kiwiclient.module.setting;

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

    public T getSValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
