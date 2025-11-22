package com.tangykiwi.kiwiclient.module.render;

import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

public class Nametags extends Module {
    public Nametags() {
        super("Nametags", "Enhanced nametags for entities.", Category.RENDER,
            new ToggleSetting("Players", "Shows nametags over players", false),
            new ToggleSetting("Animals", "Shows nametags over animals", false),
            new ToggleSetting("Mobs", "Shows nametags over mobs", false),
            new ToggleSetting("Items", "Shows nametags over items", false));
    }

    // handling done in EntityRendererMixin
}
