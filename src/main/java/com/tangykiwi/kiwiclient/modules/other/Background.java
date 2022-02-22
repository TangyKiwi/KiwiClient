package com.tangykiwi.kiwiclient.modules.other;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class Background extends Module {
    public Background() {
        super("Background", "Use custom backgrounds in menu screens", KEY_UNBOUND, Category.OTHER);
        super.toggle();
    }

    // handling done in ScreenMixin
}
