package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.OnMoveEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ElytraFly extends Module {
    public ElytraFly() {
        super("ElytraFly", "Better elytra flying", KEY_UNBOUND, Category.MOVEMENT,
            new ModeSetting("Mode", "Boost", "Control"),
            new SliderSetting("Boost", 0, 0.15, 0.05, 2).withDesc("Boost speed"),
            new SliderSetting("MaxBoost", 0, 5, 2.5, 1).withDesc("Max boost speed"),
            new SliderSetting("Control Speed", 0, 5, 0.8, 2).withDesc("Control mode speed"));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onClientMove(OnMoveEvent event) {
        if (getSetting(0).asMode().mode == 1 && mc.player.isFallFlying()) {
            if (!mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed()) {
                event.setVec(new Vec3d(event.getVec().x, 0, event.getVec().z));
            }

            if (!mc.options.backKey.isPressed() && !mc.options.leftKey.isPressed()
                    && !mc.options.rightKey.isPressed() && !mc.options.forwardKey.isPressed()) {
                event.setVec(new Vec3d(0, event.getVec().y, 0));
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent event) {
        Vec3d vec3d = new Vec3d(0, 0, getSetting(3).asSlider().getValue()).rotateY(-(float) Math.toRadians(mc.player.getYaw()));
        double currentVel = Math.abs(mc.player.getVelocity().x) + Math.abs(mc.player.getVelocity().y) + Math.abs(mc.player.getVelocity().z);
        float radianYaw = (float) Math.toRadians(mc.player.getYaw());

        float boost = getSetting(1).asSlider().getValueFloat();

        if(getSetting(0).asMode().mode == 0) {
            if (mc.player.isFallFlying() && currentVel <= getSetting(2).asSlider().getValue()) {
                if (mc.options.forwardKey.isPressed()) {
                    mc.player.addVelocity(MathHelper.sin(radianYaw) * -boost, 0, MathHelper.cos(radianYaw) * boost);
                } else if (mc.options.backKey.isPressed()) {
                    mc.player.addVelocity(MathHelper.sin(radianYaw) * boost, 0, MathHelper.cos(radianYaw) * -boost);
                } else if (mc.options.jumpKey.isPressed()) {
                    mc.player.addVelocity(0, boost, 0);
                }
            }
        }
        else if (mc.player.isFallFlying()) {
            if (mc.options.backKey.isPressed()) vec3d = vec3d.negate();
            if (mc.options.leftKey.isPressed()) vec3d = vec3d.rotateY((float) Math.toRadians(90));
            else if (mc.options.rightKey.isPressed()) vec3d = vec3d.rotateY(-(float) Math.toRadians(90));
            if (mc.options.jumpKey.isPressed()) vec3d = vec3d.add(0, getSetting(3).asSlider().getValue(), 0);
            if (mc.options.sneakKey.isPressed()) vec3d = vec3d.add(0, -getSetting(3).asSlider().getValue(), 0);

            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player.getX() + vec3d.x, mc.player.getY() - 0.01, mc.player.getZ() + vec3d.z, false));

            mc.player.setVelocity(vec3d.x, vec3d.y, vec3d.z);
        }
    }
}
