package com.tangykiwi.kiwiclient.modules.movement;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class SafeWalk extends Module {
    public SafeWalk() {
        super("SafeWalk", "Prevents you from walking off blocks.", KEY_UNBOUND, Category.MOVEMENT);
    }
}
