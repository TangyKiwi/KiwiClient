package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class FullBright extends Module {

    public FullBright() {
        super("FullBright", "Increase gamma", KEY_UNBOUND, Category.RENDER);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if(mc.options.getGamma().getValue() < 15) mc.options.getGamma().setValue(mc.options.getGamma().getValue() + 0.5);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        while(mc.options.getGamma().getValue() > 1) mc.options.getGamma().setValue(mc.options.getGamma().getValue() - 0.5);
    }
}
