package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;

public class NoRender extends Module {
    public NoRender() {
        super("NoRender", "Prevents certain things from rendering", KEY_UNBOUND, Category.RENDER,
            new ToggleSetting("Fire", true).withDesc("Modifies the on-fire overlay").withChildren(
                new SliderSetting("Opacity", 0, 1, 0.3, 1).withDesc("Opacity of the overlay"),
                new SliderSetting("Height", 0, 1, 0.4, 1).withDesc("Height of overlay")
            ),
            new ToggleSetting("Weather", true).withDesc("Disables snow/rain"),
            new ToggleSetting("Fog", true).withDesc("Disables Nether fog"));
    }

    // handling done in InGameOverlayRendererMixin
    // handling done in WorldRendererMixin
    // handling done in BackgroundRendererMixin
}
