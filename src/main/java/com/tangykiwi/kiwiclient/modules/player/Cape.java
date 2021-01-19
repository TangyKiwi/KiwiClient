package com.tangykiwi.kiwiclient.modules.player;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;

public class Cape extends Module {
    public Cape() {
        super("Cape", "Gives you a custom cape", KEY_UNBOUND, Category.PLAYER,
                new ToggleSetting("Glint", true).withDesc("Makes your cape enchanted"));
        super.toggle();
    }
}
