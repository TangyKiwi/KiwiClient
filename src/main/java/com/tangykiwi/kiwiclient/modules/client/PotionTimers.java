package com.tangykiwi.kiwiclient.modules.client;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class PotionTimers extends Module {
    public PotionTimers() {
        super("PotionTimers", "Shows remaining potion time", KEY_UNBOUND, Category.CLIENT);
    }

    // handling done in InGameHudMixin
}
