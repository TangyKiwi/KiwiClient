package com.tangykiwi.kiwiclient.modules.client;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;

public class Tooltips extends Module {

    public Tooltips() {
        super("Tooltips", "Displays even more advanced tooltips", KEY_UNBOUND, Category.CLIENT,
            new ToggleSetting("Suspicious Stew", true).withDesc("Shows effect and duration of suspicious stew")
        );
        super.toggle();
    }
}
