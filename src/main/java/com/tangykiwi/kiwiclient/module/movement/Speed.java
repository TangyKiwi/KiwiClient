package com.tangykiwi.kiwiclient.module.movement;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

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

        if (mc.options.keyShift.isDown())
            return;

        if ((mc.player.zza  != 0 || mc.player.xxa != 0)) {
            if (!mc.player.isSprinting()) {
                mc.player.setSprinting(true);
            }

            double multi = getSetting(0).asSlider().getValue() + 1.0;
            Vec3 vel = mc.player.getDeltaMovement();
            mc.player.setDeltaMovement(vel.x * multi, vel.y, vel.z * multi);

            if (getSetting(1).asToggle().getValue() && mc.player.onGround()) {
                mc.player.jumpFromGround();
            }
        }
    }
}
