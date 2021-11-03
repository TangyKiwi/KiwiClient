package com.tangykiwi.kiwiclient.modules.client;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;

public class Time extends Module {
    public Time() {
        super("Time", "Changes the time of day client side", KEY_UNBOUND, Category.CLIENT,
            new SliderSetting("Time", 0, 24, 12, 0).withDesc("Time of day"));
    }
}
