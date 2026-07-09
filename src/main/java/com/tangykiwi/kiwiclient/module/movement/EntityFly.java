package com.tangykiwi.kiwiclient.module.movement;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityFly extends Module {
    public EntityFly() {
        super("EntityFly", "Fly when riding entities", Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        if (mc.player.getVehicle() == null) return;

        Entity vehicle = mc.player.getVehicle();
        Vec3 velocity = vehicle.getDeltaMovement();
        double multY = mc.options.keyJump.isDown() ? 0.3 : 0;
        vehicle.setDeltaMovement(new Vec3(velocity.x, multY, velocity.z));
    }
}
