package com.tangykiwi.kiwiclient.module.client;

import com.tangykiwi.kiwiclient.gui.clickgui.ClickGUIScreen;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.mojang.blaze3d.platform.InputConstants;

public class ClickGUI extends Module {
    public SliderSetting length = new SliderSetting("Length", "Maximum window module length", 1, 24, 24, 0);

    public ClickGUI() {
        super("ClickGUI", "Renders the ClickGUI", InputConstants.KEY_SEMICOLON, Category.CLIENT);
        // this.addSetting(length);
    }

    @Override
    public void onEnable() {
        mc.setScreenAndShow(ClickGUIScreen.INSTANCE);
        setEnabled(false);
    }

    @Override
    public void onDisable() {
        if (mc.gui.screen() instanceof ClickGUIScreen) {
            mc.setScreenAndShow(null);
        }
    }
}
