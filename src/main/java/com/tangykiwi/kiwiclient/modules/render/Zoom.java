package com.tangykiwi.kiwiclient.modules.render;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class Zoom extends Module {

    private final double defaultLevel = 3;
    private Double currentLevel;
    private Double defaultMouseSensitivity;

    public Zoom() {
        super("Zoom", "Changes your FOV, use mouse scroll to change strength", GLFW.GLFW_MOUSE_BUTTON_5, Category.RENDER);
    }

    public double changeFovBasedOnZoom(double fov)
    {
        GameOptions gameOptions = mc.options;

        if(currentLevel == null)
            currentLevel = defaultLevel;

        if(!KiwiClient.zoomKey.isPressed())
        {
            disable();
            currentLevel = defaultLevel;

            if(defaultMouseSensitivity != null)
            {
                gameOptions.getMouseSensitivity().setValue(defaultMouseSensitivity);
                defaultMouseSensitivity = null;
            }

            return fov;
        }

        if(defaultMouseSensitivity == null)
            defaultMouseSensitivity = gameOptions.getMouseSensitivity().getValue();

        // Adjust mouse sensitivity in relation to zoom level.
        // (fov / currentLevel) / fov is a value between 0.02 (50x zoom)
        // and 1 (no zoom).
        enable();

        gameOptions.getMouseSensitivity().setValue(defaultMouseSensitivity * (fov / currentLevel / fov));

        return fov / currentLevel;
    }

    public void onMouseScroll(double amount)
    {
        if(!KiwiClient.zoomKey.isPressed()) {
            disable();
            return;
        }

        if(currentLevel == null)
            currentLevel = defaultLevel;

        if(amount > 0)
            currentLevel *= 1.1;
        else if(amount < 0)
            currentLevel *= 0.9;

        currentLevel = MathHelper.clamp(currentLevel, 1, 50);
    }

}
