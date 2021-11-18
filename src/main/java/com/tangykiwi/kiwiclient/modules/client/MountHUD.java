package com.tangykiwi.kiwiclient.modules.client;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class MountHUD extends Module {
    public MountHUD() {
        super("MountHUD", "Changes your HUD when on a mount", KEY_UNBOUND, Category.CLIENT);
    }

    // handling done in InGameHudMixin
}
