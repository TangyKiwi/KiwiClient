package com.tangykiwi.kiwiclient.modules.movement;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class NoWorldBorder extends Module {
    public NoWorldBorder() {
        super("NoWorldBorder", "Lets you move past the world border", KEY_UNBOUND, Category.MOVEMENT);
    }

    // handling done in EntityMixin
}
