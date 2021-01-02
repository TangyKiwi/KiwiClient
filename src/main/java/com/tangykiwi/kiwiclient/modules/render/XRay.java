package com.tangykiwi.kiwiclient.modules.render;

import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import org.lwjgl.glfw.GLFW;

public class XRay extends Module {

    public XRay() {
        super("XRay", "Shows ores", GLFW.GLFW_KEY_C, Category.RENDER);
    }
}
