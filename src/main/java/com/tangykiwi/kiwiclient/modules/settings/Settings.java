package com.tangykiwi.kiwiclient.modules.settings;

public abstract class Settings {
    
    protected String description = "";

    public SettingToggle asToggle() {
        try {
            return (SettingToggle) this;
        } catch (Exception e) {
            throw new ClassCastException("Exception parsing setting: " + this);
        }
    }

    public abstract String getName();

    public String getDesc() {
        return description;
    }
}
