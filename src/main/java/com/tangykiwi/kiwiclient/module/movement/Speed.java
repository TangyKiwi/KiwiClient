package com.tangykiwi.kiwiclient.module.movement;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Vec3d;

public class Speed extends Module {
    public Speed() {
        super("Speed", "Move faster", Category.MOVEMENT,
            new SliderSetting("Speed", "Speed multiplier", 0.1, 1, 0.5, 2),
            new ToggleSetting("Bhop", "Bunny hop", false));
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

            if (getSetting(1).asToggle().getValue() && vel >= 0.12 && mc.player.isOnGround()) {
                mc.player.updateVelocity(vel >= 0.3 ? 0.0f : 0.15f, new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
                mc.player.jump();
            }
        }
    }
}
