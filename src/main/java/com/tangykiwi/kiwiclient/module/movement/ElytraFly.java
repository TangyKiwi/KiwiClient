package com.tangykiwi.kiwiclient.module.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.PlayerMoveEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ModeSetting;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class ElytraFly extends Module {
    public ElytraFly() {
        super("ElytraFly", "Better elytra flight", Category.MOVEMENT,
            new ModeSetting("Mode", "Mode", "Boost", "Control"),
            new SliderSetting("Boost", "Boost multiplier", 0, 0.15, 0.05, 2),
            new SliderSetting("MaxBoost", "Max boost speed", 0, 5, 2.5, 1),
            new SliderSetting("Control Speed", "Control speed multiplier", 0, 5, 0.8, 2)
        );
    }

    @Subscribe
    public void onPlayerMove(PlayerMoveEvent event) {
        if (getSetting(0).asMode().getValue() == 1 && mc.player.isFallFlying()) {
            Vec3 currentVec = mc.player.getDeltaMovement();
            if (!mc.options.keyJump.isDown() && !mc.options.keyShift.isDown()) {
                mc.player.setDeltaMovement(currentVec.x, 0, currentVec.z);
                // event.setVec(new Vec3(event.getVec().x, 0, event.getVec().z));
            }

            if (!mc.options.keyDown.isDown() && !mc.options.keyLeft.isDown()
                    && !mc.options.keyRight.isDown() && !mc.options.keyUp.isDown()) {
                mc.player.setDeltaMovement(0, event.getVec().y, 0);
                // event.setVec(new Vec3(0, event.getVec().y, 0));
            }
        }
    }

    @Subscribe
    public void onTick(TickEvent event) {
        if (mc.player == null) return;

        Vec3 vel = new Vec3(0, 0, getSetting(3).asSlider().getValue()).yRot(-(float) Math.toRadians(mc.player.getYRot()));
        Vec3 currentVec = mc.player.getDeltaMovement();
        double currentVel = Math.abs(mc.player.getDeltaMovement().x) + Math.abs(mc.player.getDeltaMovement().y) + Math.abs(mc.player.getDeltaMovement().z);
        float radianYaw = (float) Math.toRadians(mc.player.getYRot());

        float boost = getSetting(1).asSlider().getValueFloat();

        if(getSetting(0).asMode().getValue() == 0) {
            if (mc.player.isFallFlying() && currentVel <= getSetting(2).asSlider().getValue()) {
                if (mc.options.keyUp.isDown()) {
                    mc.player.setDeltaMovement(currentVec.x + Mth.sin(radianYaw) * -boost, currentVec.y, currentVec.z + Mth.cos(radianYaw) * boost);
                } else if (mc.options.keyDown.isDown()) {
                    mc.player.setDeltaMovement(currentVec.x + Mth.sin(radianYaw) * boost, currentVec.y, currentVec.z + Mth.cos(radianYaw) * -boost);
                } else if (mc.options.keyJump.isDown()) {
                    mc.player.setDeltaMovement(0, boost, 0);
                }
            }
        }
        else if (mc.player.isFallFlying()) {
            if (mc.options.keyDown.isDown()) vel = vel.reverse();
            if (mc.options.keyLeft.isDown()) vel = vel.yRot((float) Math.toRadians(90));
            else if (mc.options.keyRight.isDown()) vel = vel.yRot(-(float) Math.toRadians(90));
            if (mc.options.keyJump.isDown()) vel = vel.add(0, getSetting(3).asSlider().getValue(), 0);
            if (mc.options.keyShift.isDown()) vel = vel.add(0, -getSetting(3).asSlider().getValue(), 0);

            mc.getConnection().send(
                new ServerboundMovePlayerPacket.Pos(
                    mc.player.getX() + vel.x,
                    mc.player.getY() - 0.01,
                    mc.player.getZ() + vel.z,
                    false,
                    mc.player.horizontalCollision
                )
            );

            mc.player.setDeltaMovement(vel.x, vel.y, vel.z);
        }
    }
}
