package com.tangykiwi.kiwiclient.module.client;

import com.tangykiwi.kiwiclient.gui.ClickGUIScreen;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import org.lwjgl.glfw.GLFW;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class ClickGUI extends Module {
    public static ClickGUIScreen clickGUIScreen = new ClickGUIScreen();

    public ClickGUI() {
        super("ClickGUI", "Renders the ClickGUI", GLFW.GLFW_KEY_SEMICOLON, Category.CLIENT);
    }

    @Override
    public void onEnable() {
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
