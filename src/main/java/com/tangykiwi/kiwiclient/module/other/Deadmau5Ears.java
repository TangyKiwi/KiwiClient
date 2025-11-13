package com.tangykiwi.kiwiclient.module.other;

import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

public class Deadmau5Ears extends Module {
    public Deadmau5Ears() {
        super("Deadmau5Ears", "Adds Deadmau5 ears to players", Category.OTHER,
            new ToggleSetting("Glint", "Enchanted cape effect", true));
    }

    // handling done in Deadmau5EarsFeatureRendererMixin
}
