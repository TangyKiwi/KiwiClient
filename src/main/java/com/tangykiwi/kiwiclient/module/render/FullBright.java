package com.tangykiwi.kiwiclient.module.render;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixininterface.ISimpleOption;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.client.OptionInstance;

public class FullBright extends Module {
    public FullBright() {
        super("FullBright", "Increases gamma", Category.RENDER);
    }

    @Subscribe
    public void onTick(TickEvent.Post e) {
        if (mc.options.gamma().get() < 16) {
            OptionInstance<Double> gammaOption = mc.options.gamma();
            @SuppressWarnings("unchecked")
            ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
            gammaOption2.forceSetValue(gammaOption.get() + 0.5);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        OptionInstance<Double> gammaOption = mc.options.gamma();
        @SuppressWarnings("unchecked")
        ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
        gammaOption2.forceSetValue(1.0);
    }
}
