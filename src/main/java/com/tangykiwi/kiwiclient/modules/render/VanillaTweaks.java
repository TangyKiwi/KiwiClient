package com.tangykiwi.kiwiclient.modules.render;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;

public class VanillaTweaks extends Module {
    public VanillaTweaks() {
        super("VanillaTweaks", "Custom resource pack", KEY_UNBOUND, Category.RENDER,
            new ToggleSetting("OreBorders", false).withDesc("Ores have borders"));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.getResourcePackManager().createResourcePacks();
    }

}
