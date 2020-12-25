package com.tangykiwi.kiwiclient;

import com.google.common.eventbus.EventBus;

import com.tangykiwi.kiwiclient.modules.ModuleManager;
import com.tangykiwi.kiwiclient.util.DiscordRP;
import net.fabricmc.api.ModInitializer;

public class KiwiClient implements ModInitializer {

    public static final String MOD_ID = "kiwiclient";
    public static String name = "KiwiClient", version = "1.4.5";

    public static ModuleManager moduleManager;
    public static DiscordRP discordRPC;
    public static EventBus eventBus = new EventBus();

    @Override
    public void onInitialize() {
        moduleManager = new ModuleManager();
        moduleManager.init();

        discordRPC = new DiscordRP();
        discordRPC.start();

        eventBus.register(moduleManager);
    }
}
