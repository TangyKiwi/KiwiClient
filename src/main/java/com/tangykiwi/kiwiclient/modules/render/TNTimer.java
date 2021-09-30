package com.tangykiwi.kiwiclient.modules.render;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class TNTimer extends Module {

    public TNTimer() {
        super("TNTimer", "Displays TNT fuse countdown", KEY_UNBOUND, Category.RENDER);
    }

    // handling done in TntEntityRendererMixin
}
