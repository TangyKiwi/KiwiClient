package com.tangykiwi.kiwiclient.modules.other;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class LoadingScreen extends Module {
    public LoadingScreen() {
        super("LoadingScreen", "Use the custom loading screen", KEY_UNBOUND, Category.OTHER);
        super.toggle();
    }

    // handling done in SplashScreenMixin
}

