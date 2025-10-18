package com.tangykiwi.kiwiclient.module.movement;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.ClientPlayerEntityAccessor;
import com.tangykiwi.kiwiclient.mixin.PlayerMoveC2SPacketAccessor;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ModeSetting;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;

import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.util.math.Vec3d;

public class Fly extends Module{
    private boolean flip;
    private float lastYaw;
    private double lastPacketY = Double.MAX_VALUE;
    private int delayLeft = 20;
    private int offLeft = 1;

    public Fly() {
        super("Fly", "Allows you to fly", Category.MOVEMENT,
            new ModeSetting("Mode", "Fly mode", "Simple", "Velocity"),
            new ModeSetting("AntiKick", "AntiKick mode", "None", "Toggle", "Packet"),
            new SliderSetting("Speed", "Fly speed", 0.1, 5, 1, 1));
    }

    @Override
    public void onEnable() {
        if(getSetting(0).asMode().getValue() == 0 && !mc.player.isSpectator()) {
            float speed = getSetting(2).asSlider().getValueFloat();
            mc.player.getAbilities().setFlySpeed(speed / 10);
            mc.player.getAbilities().allowFlying = true;
            mc.player.getAbilities().flying = true;
        }

        super.onEnable();
    }

    @Override
    public void onDisable() {
        if(getSetting(0).asMode().getValue() == 0 && !mc.player.isSpectator()) {
            abilitiesOff();
            mc.player.getAbilities().setFlySpeed(0.05f);
        }

        super.onDisable();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPreTick(TickEvent.Pre event) {
        float currentYaw = mc.player.getYaw();
        if (mc.player.fallDistance >= 3f && currentYaw == lastYaw && mc.player.getVelocity().length() < 0.003d) {
            mc.player.setYaw(currentYaw + (flip ? 1 : -1));
            flip = !flip;
        }
        lastYaw = currentYaw;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPostTick(TickEvent.Post event) {
        if (delayLeft > 0) delayLeft--;

        if (offLeft <= 0 && delayLeft <= 0) {
            delayLeft = 20;
            offLeft = 1;

            if (getSetting(1).asMode().getValue() == 2) {
                // Resend movement packets
                ((ClientPlayerEntityAccessor) mc.player).setTicksSinceLastPositionPacketSent(20);
            }
        } else if (delayLeft <= 0) {
            boolean shouldReturn = false;

            if (getSetting(1).asMode().getValue() == 1) {
                if (getSetting(0).asMode().getValue() == 0) {
                    abilitiesOff();
                    shouldReturn = true;
                }
            } else if (getSetting(1).asMode().getValue() == 2 && offLeft == 1) {
                // Resend movement packets
                ((ClientPlayerEntityAccessor) mc.player).setTicksSinceLastPositionPacketSent(20);
            }

            offLeft--;

            if (shouldReturn) return;
        }

        if (mc.player.getYaw() != lastYaw) mc.player.setYaw(lastYaw);

        float speed = getSetting(2).asSlider().getValueFloat();

        if (getSetting(0).asMode().getValue() == 1) {
            mc.player.getAbilities().flying = false;
            mc.player.setVelocity(0, 0, 0);
            Vec3d initialVelocity = mc.player.getVelocity();
            if (mc.options.jumpKey.isPressed())
                mc.player.setVelocity(initialVelocity.add(0, speed * 5f / 10, 0));
            if (mc.options.sneakKey.isPressed())
                mc.player.setVelocity(initialVelocity.subtract(0, speed * 5f / 10, 0));
        } else if (getSetting(0).asMode().getValue() == 0) {
            if (mc.player.isSpectator()) return;
            mc.player.getAbilities().setFlySpeed(speed / 10);
            mc.player.getAbilities().flying = true;
            if (mc.player.getAbilities().creativeMode) return;
            mc.player.getAbilities().allowFlying = true;
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPacketSend(PacketEvent.Send event) {
        if (!(event.packet instanceof PlayerMoveC2SPacket packet) || getSetting(1).asMode().getValue() != 2) return;

        double currentY = packet.getY(Double.MAX_VALUE);
        if (currentY != Double.MAX_VALUE) {
            antiKickPacket(packet, currentY);
        } else {
            // if the packet is a LookAndOnGround packet or an OnGroundOnly packet then we need to
            // make it a Full packet or a PositionAndOnGround packet respectively, so it has a Y value
            PlayerMoveC2SPacket fullPacket;
            if (packet.changesLook()) {
                fullPacket = new PlayerMoveC2SPacket.Full(
                    mc.player.getX(),
                    mc.player.getY(),
                    mc.player.getZ(),
                    packet.getYaw(0),
                    packet.getPitch(0),
                    packet.isOnGround(),
                    mc.player.horizontalCollision
                );
            } else {
                fullPacket = new PlayerMoveC2SPacket.PositionAndOnGround(
                    mc.player.getX(),
                    mc.player.getY(),
                    mc.player.getZ(),
                    packet.isOnGround(),
                    mc.player.horizontalCollision
                );
            }
            event.setCancelled(true);
            antiKickPacket(fullPacket, mc.player.getY());
            mc.getNetworkHandler().sendPacket(fullPacket);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!(event.packet instanceof PlayerAbilitiesS2CPacket packet) || getSetting(0).asMode().getValue() == 0) return;
        event.cancel(); // Cancel packet, so fly won't be toggled

        mc.player.getAbilities().invulnerable = packet.isInvulnerable();
        mc.player.getAbilities().creativeMode = packet.isCreativeMode();
        mc.player.getAbilities().setWalkSpeed(packet.getWalkSpeed());
    }

    private void antiKickPacket(PlayerMoveC2SPacket packet, double currentY) {
        // maximum time we can be "floating" is 80 ticks, so 4 seconds max
        if (this.delayLeft <= 0 && this.lastPacketY != Double.MAX_VALUE &&
                shouldFlyDown(currentY, this.lastPacketY) && isEntityOnAir(mc.player)) {
            // actual check is for >= -0.03125D, but we have to do a bit more than that
            // due to the fact that it's a bigger or *equal* to, and not just a bigger than
            ((PlayerMoveC2SPacketAccessor) packet).setY(lastPacketY - 0.03130D);
        } else {
            lastPacketY = currentY;
        }
    }

    private boolean shouldFlyDown(double currentY, double lastY) {
        if (currentY >= lastY) {
            return true;
        } else return lastY - currentY < 0.03130D;
    }

    private boolean isEntityOnAir(Entity entity) {
        return entity.getWorld().getStatesInBox(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0)).allMatch(AbstractBlock.AbstractBlockState::isAir);
    }

    public float getOffGroundSpeed() {
        // All the multiplication below is to get the speed to roughly match the speed you get when using vanilla fly

        if (!isEnabled() || getSetting(0).asMode().getValue() != 1) return -1;
        return getSetting(2).asSlider().getValueFloat();
    }

    public void abilitiesOff() {
        mc.player.getAbilities().flying = false;
        if (mc.player.getAbilities().creativeMode) return;
        mc.player.getAbilities().allowFlying = false;
    }
}
