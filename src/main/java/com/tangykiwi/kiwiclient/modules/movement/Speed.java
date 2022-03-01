package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {

    public Speed() {
        super("Speed", "Go fast", GLFW.GLFW_KEY_GRAVE_ACCENT, Category.MOVEMENT,
            new SliderSetting("Speed", 0.1, 1, 0.5, 2).withDesc("Speed ^%"),
            new ToggleSetting("Bhop", false));
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if (mc.options.sneakKey.isPressed())
            return;

        if ((mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0)) {
            if (!mc.player.isSprinting()) {
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
            }

            mc.player.setVelocity(new Vec3d(0, mc.player.getVelocity().y, 0));
            mc.player.updateVelocity(getSetting(0).asSlider().getValueFloat(),
                    new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));

            double vel = Math.abs(mc.player.getVelocity().getX()) + Math.abs(mc.player.getVelocity().getZ());

            if (getSetting(1).asToggle().state && vel >= 0.12 && mc.player.isOnGround()) {
                mc.player.updateVelocity(vel >= 0.3 ? 0.0f : 0.15f, new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
                mc.player.jump();
            }
        }
    }
}
