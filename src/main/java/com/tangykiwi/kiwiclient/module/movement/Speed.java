package com.tangykiwi.kiwiclient.module.movement;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Speed extends Module {
    public Speed() {
        super("Speed", "Move faster", Category.MOVEMENT,
            new SliderSetting("Speed", "Speed multiplier", 0.1, 1, 0.5, 2),
            new ToggleSetting("Bhop", "Bunny hop", false));
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if (mc.player == null) return;

        if (mc.options.keyShift.isDown()) return;

        if (mc.player.zza != 0 || mc.player.xxa != 0) {
            if (!mc.player.isSprinting()) {
                mc.player.setSprinting(true);
            }

            Vec3 vel = mc.player.getDeltaMovement();
            float yaw = Mth.wrapDegrees(mc.player.getYRot()); // degrees
            double yawRad = Math.toRadians(yaw); // radians
            
            float strafe = mc.player.xxa;
            float forward = mc.player.zza;
            float newStrafe = strafe * Mth.cos((float) yawRad) - forward * Mth.sin((float) yawRad);
            float newForward = forward * Mth.cos((float) yawRad) + strafe * Mth.sin((float) yawRad);

            mc.player.setDeltaMovement(new Vec3(0, vel.y, 0));
            mc.player.addDeltaMovement(new Vec3(newStrafe, 0, newForward).scale(getSetting("Speed").asSlider().getValue()));

            double newvel = Math.abs(mc.player.getDeltaMovement().x) + Math.abs(mc.player.getDeltaMovement().z);

            if (getSetting(1).asToggle().getValue() && newvel >= 0.12 && mc.player.onGround()) {
                mc.player.addDeltaMovement(new Vec3(newStrafe, 0, newForward).scale(newvel > 0.3 ? 0.0f : 0.15f));
                mc.player.jumpFromGround();
            }
        }
    }
}
