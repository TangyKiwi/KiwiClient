package com.tangykiwi.kiwiclient.modules.player;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class Deadmau5Ears extends Module {
    public Deadmau5Ears() {
        super("Deadmau5Ears", "Gives you ears like Deadmau5", KEY_UNBOUND, Category.PLAYER);
        super.toggle();
    }
}
