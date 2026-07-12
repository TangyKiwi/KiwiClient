package com.tangykiwi.kiwiclient.module.other;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.LevelRenderEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

public class DummyModule extends Module {
    public DummyModule() {
        super("DummyModule", "Dummy Module", -1, Category.OTHER);
    }

    @Subscribe
    public void onWorldRender(LevelRenderEvent event) {
        RenderUtils.drawBoxFilled(event.getSubmitNodeStorage(), mc.player.blockPosition().below(1), 0x7F00FF00);
        RenderUtils.drawBoxOutline(event.getSubmitNodeStorage(), mc.player.blockPosition().below(1), 0xFFFF0000, 4);
    }
}
