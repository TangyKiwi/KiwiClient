package com.tangykiwi.kiwiclient.modules.other;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;

public class MainMenu extends Module {
    public MainMenu() {
        super("MainMenu", "Use the custom main menu", KEY_UNBOUND, Category.OTHER,
            new ModeSetting("Background", "Kiwi", "Arrxw").withDesc("Main menu background"),
            new ModeSetting("Player", "Head", "Model").withDesc("Player preview type"));
        super.toggle();
        super.runInMainMenu = true;
    }

    // handling done in TitleScreenMixin & MainMenu
}
