package com.tangykiwi.kiwiclient.modules.combat;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class AutoClicker extends Module {
    public AutoClicker() {
        super("Autoclicker", "Clicks really fast", KEY_UNBOUND, Category.COMBAT);
    }


    @Subscribe
    public void onTick(TickEvent event)
    {
        mc.options.keyAttack.setPressed(true);
    }

}
