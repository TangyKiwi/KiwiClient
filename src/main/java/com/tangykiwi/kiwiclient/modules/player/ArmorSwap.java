package com.tangykiwi.kiwiclient.modules.player;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class ArmorSwap extends Module {
    public ArmorSwap() {
        super("ArmorSwap", "Hotswaps your armor on right click", KEY_UNBOUND, Category.PLAYER);
    }
}
