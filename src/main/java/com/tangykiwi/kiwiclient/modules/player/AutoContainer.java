package com.tangykiwi.kiwiclient.modules.player;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;

public class AutoContainer extends Module {
    public AutoContainer() {
        super("AutoContainer", "Automatically steals / dumps items from a container", KEY_UNBOUND, Category.PLAYER,
            new SliderSetting("Delay", 0, 500, 100, 0),
            new SliderSetting("Random", 0, 100, 50, 0));
    }
    public int getDelay() {
        return getSetting(0).asSlider().getValueInt() + (int) (Math.random() * (getSetting(1).asSlider().getValue()));
    }

    // handling done in GenericContainerScreenMixin & ShulkerBoxScreenMixin
}
