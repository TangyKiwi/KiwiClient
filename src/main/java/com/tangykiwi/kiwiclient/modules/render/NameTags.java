package com.tangykiwi.kiwiclient.modules.render;

import com.tangykiwi.kiwiclient.modules.Category;
import org.lwjgl.glfw.GLFW;

public class NameTags extends Module{
    public FullBright() {
        super("NameTags", "Better nametags for players", GLFW.GLFW_KEY_N, Category.RENDER);
    }

}
