package com.tangykiwi.kiwiclient.modules.player;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;

public class AntiBlind extends Module {
    public AntiBlind() {
        super("AntiBlind", "Prevents blindness / darkness", KEY_UNBOUND, Category.PLAYER);
    }

    // handling done in WorldRendererMixin
    // handling done in BackgroundRendererMixin
}
