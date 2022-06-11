package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixininterface.ISimpleOption;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.option.SimpleOption;

public class FullBright extends Module {

    public FullBright() {
        super("FullBright", "Increase gamma", KEY_UNBOUND, Category.RENDER);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if(mc.options.getGamma().getValue() < 16) {
            SimpleOption<Double> gammaOption = mc.options.getGamma();
            @SuppressWarnings("unchecked")
            ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
            gammaOption2.forceSetValue(gammaOption.getValue() + 0.5);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        while(mc.options.getGamma().getValue() > 1) {
            SimpleOption<Double> gammaOption = mc.options.getGamma();
            @SuppressWarnings("unchecked")
            ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
            gammaOption2.forceSetValue(gammaOption.getValue() - 0.5);
        }
    }
}
