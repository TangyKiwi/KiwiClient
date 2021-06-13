package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {

    public Speed() {
        super("Speed", "Go fast", GLFW.GLFW_KEY_GRAVE_ACCENT, Category.MOVEMENT,
                new SliderSetting("Speed", 0.1, 10, 2, 2));
    }

    @Subscribe
    public void onTick(TickEvent e) {
        double forward = mc.player.forwardSpeed;
        double strafe = mc.player.sidewaysSpeed;
        float yaw = mc.player.getYaw();

        double speedstrafe = getSetting(0).asSlider().getValue() / 3;

        if (!mc.player.isFallFlying()) {
            if ((forward == 0.0D) && (strafe == 0.0D)) mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
            else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) yaw += (forward > 0.0D ? -45 : 45);
                    else if (strafe < 0.0D) yaw += (forward > 0.0D ? 45 : -45);

                    strafe = 0.0D;
                    if (forward > 0.0D) forward = 1.0D;
                    else if (forward < 0.0D) forward = -1.0D;
                }
                mc.player.setVelocity((forward * speedstrafe * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speedstrafe * Math.sin(Math.toRadians(yaw + 90.0F))), mc.player.getVelocity().y,
                        forward * speedstrafe * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speedstrafe * Math.cos(Math.toRadians(yaw + 90.0F)));
            }
        }
    }
}
