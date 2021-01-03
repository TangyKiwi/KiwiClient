package com.tangykiwi.kiwiclient.modules.client;

import com.tangykiwi.kiwiclient.gui.ClickGuiScreen;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.Category;
import org.lwjgl.glfw.GLFW;

public class ClickGui extends Module {

    public static ClickGuiScreen clickGui = new ClickGuiScreen();

    public ClickGui() {
    super("ClickGui", "Draws the ClickGui", GLFW.GLFW_KEY_RIGHT_SHIFT, Category.CLIENT);
    }

    public void onEnable() {
        mc.openScreen(clickGui);
        setToggled(false);
    }
}
