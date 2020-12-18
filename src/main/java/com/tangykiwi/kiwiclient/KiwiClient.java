package com.tangykiwi.kiwiclient;

import com.google.common.eventbus.EventBus;

import com.tangykiwi.kiwiclient.modules.ModuleManager;
import com.tangykiwi.kiwiclient.registry.ModItems;
import net.fabricmc.api.ModInitializer;

public class KiwiClient implements ModInitializer {

    public static final String MOD_ID = "kiwiclient";
    public static String name = "KiwiClient", version = "1.4.3";

    public static ModuleManager moduleManager;
    public static EventBus eventBus = new EventBus();

    @Override
    public void onInitialize() {
        ModItems.registerItems();

        moduleManager = new ModuleManager();
        moduleManager.init();
        eventBus.register(moduleManager);
    }
}
