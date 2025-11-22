package com.tangykiwi.kiwiclient.module.render;

import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;
import com.tangykiwi.kiwiclient.module.Category;

public class NoRender extends Module {
    public NoRender() {
        super("NoRender", "Prevents certain things from rendering", Category.RENDER,
            new ToggleSetting("Weather", "Disables snow/rain", true),
            new ToggleSetting("Fog", "Disables fog", true),
            new ToggleSetting("Blindness", "Disables blindness effect overlay", true),
            new ToggleSetting("Darkness", "Disables darkness effect overlay", true));
    }

    // handling done in WorldRendererMixin for weather
    // handling done in FogRendererMixin for fog
}
