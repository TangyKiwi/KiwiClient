package com.tangykiwi.kiwiclient;

import com.tangykiwi.kiwiclient.modules.ModuleManager;
import com.tangykiwi.kiwiclient.registry.ModItems;
import net.fabricmc.api.ModInitializer;

public class KiwiClient implements ModInitializer {

    public static final String MOD_ID = "kiwiclient";
    public static String name = "KiwiClient", version = "1.0.0";

    public static ModuleManager moduleManager;

    @Override
    public void onInitialize() {
        ModItems.registerItems();
        moduleManager = new ModuleManager();
        moduleManager.init();
    }
}
