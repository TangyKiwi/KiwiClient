package com.tangykiwi.kiwiclient.modules.other;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class NoIP extends Module {
    public NoIP() {
        super("NoIP", "Hides the ip of the server you're on in DiscordRPC", KEY_UNBOUND, Category.OTHER);
    }

    // handling done in MinecraftClientMixin
}
