package com.tangykiwi.kiwiclient.module.client;

import com.tangykiwi.kiwiclient.gui.clickgui.ClickGUIScreen;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import org.lwjgl.glfw.GLFW;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class ClickGUI extends Module {
    public SliderSetting length = new SliderSetting("Length", "Maximum window module length", 1, 24, 24, 0);

    public ClickGUI() {
        super("ClickGUI", "Renders the ClickGUI", GLFW.GLFW_KEY_SEMICOLON, Category.CLIENT);
        this.addSetting(length);
    }

    @Override
    public void onEnable() {
        mc.setScreen(ClickGUIScreen.INSTANCE);
        setEnabled(false);
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen instanceof ClickGUIScreen) {
            mc.setScreen(null);
        }
    }
}
