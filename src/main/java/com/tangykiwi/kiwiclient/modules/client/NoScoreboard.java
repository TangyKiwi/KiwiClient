package com.tangykiwi.kiwiclient.modules.client;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class NoScoreboard extends Module {
    public NoScoreboard() {
        super("NoScoreboard", "Hides the scoreboard", KEY_UNBOUND, Category.CLIENT);
    }

    // handling done in InGameHudMixin
}
