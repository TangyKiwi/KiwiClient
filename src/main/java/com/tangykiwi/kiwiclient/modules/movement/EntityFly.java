package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityFly extends Module {
    public EntityFly() {
        super("EntityFly", "Fly when riding entities", KEY_UNBOUND, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if(!mc.player.hasVehicle()) return;

        Entity vehicle = mc.player.getVehicle();
        Vec3d velocity = vehicle.getVelocity();
        double multY = mc.options.jumpKey.isPressed() ? 0.3 : 0;
        vehicle.setVelocity(velocity.x, multY, velocity.z);
    }
}
