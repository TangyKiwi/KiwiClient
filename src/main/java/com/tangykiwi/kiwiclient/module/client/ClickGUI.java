package com.tangykiwi.kiwiclient.module.client;

import com.tangykiwi.kiwiclient.gui.clickgui.ClickGUIScreen;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import org.lwjgl.glfw.GLFW;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class ClickGUI extends Module {
    public static ClickGUIScreen clickGUIScreen;
    private boolean firstEnable = true;

    public ClickGUI() {
        super("ClickGUI", "Renders the ClickGUI", GLFW.GLFW_KEY_SEMICOLON, Category.CLIENT);
    }

    @Override
    public void onEnable() {
        if (firstEnable) {
            clickGUIScreen = new ClickGUIScreen();
            firstEnable = false;
        }
        mc.setScreen(clickGUIScreen);
        setEnabled(false);
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen instanceof ClickGUIScreen) {
            mc.setScreen(null);
        }
    }
}
