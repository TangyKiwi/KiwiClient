package com.tangykiwi.kiwiclient.module.movement;

import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

public class SafeWalk extends Module {
    public SafeWalk() {
        super("SafeWalk", "Prevents you from falling off edges", Category.MOVEMENT,
            new ToggleSetting("Sneak", "Sneak when near edges", true));
    }

    // handling done in PlayerMixin
}
