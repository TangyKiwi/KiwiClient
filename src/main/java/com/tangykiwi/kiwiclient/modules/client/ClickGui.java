package com.tangykiwi.kiwiclient.modules.client;

import com.tangykiwi.kiwiclient.gui.clickgui.ModuleClickGuiScreen;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import org.lwjgl.glfw.GLFW;

public class ClickGui extends Module {

    public static ModuleClickGuiScreen clickGui = new ModuleClickGuiScreen();

    public ClickGui() {
    super("ClickGui", "Draws the ClickGui", GLFW.GLFW_KEY_SEMICOLON, Category.CLIENT);
    }

    @Override
    public void onEnable() {
        mc.setScreen(clickGui);
        setToggled(false);
    }

    @Override
    public void onDisable() {
        if (mc.currentScreen instanceof ModuleClickGuiScreen) {
            mc.setScreen(null);
        }
    }
}
