package com.tangykiwi.kiwiclient.module.player;

import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;

public class FastBreak extends Module{
    public FastBreak() {
        super("FastBreak", "Break blocks instantly", Category.PLAYER);
    }

    // handling done in ClientPlayerInteractionManagerMixin
}
