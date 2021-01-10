package com.tangykiwi.kiwiclient.modules.player;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.OnMoveEvent;
import com.tangykiwi.kiwiclient.event.OpenScreenEvent;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.FakeEntity;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Freecam extends Module {
    private FakeEntity dummy;
    private double[] playerPos;
    private float[] playerRot;
    private Entity riding;

    private boolean prevFlying;
    private float prevFlySpeed;

    public Freecam() {
        super("Freecam", "Detaches your camera.", GLFW.GLFW_KEY_U, Category.PLAYER,
                new SliderSetting("Speed", 0, 3, 0.5, 2));
    }

    @Override
    public void onEnable() {
        playerPos = new double[]{mc.player.getX(), mc.player.getY(), mc.player.getZ()};
        playerRot = new float[]{mc.player.yaw, mc.player.pitch};

        dummy = new FakeEntity();
        dummy.copyPositionAndRotation(mc.player);
        dummy.setBoundingBox(dummy.getBoundingBox().expand(0.1));

        dummy.spawn();

        if (mc.player.getVehicle() != null) {
            riding = mc.player.getVehicle();
            mc.player.getVehicle().removeAllPassengers();
        }

        if (mc.player.isSprinting()) {
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }

        prevFlying = mc.player.abilities.flying;
        prevFlySpeed = mc.player.abilities.getFlySpeed();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        dummy.despawn();
        mc.player.noClip = false;
        mc.player.abilities.flying = prevFlying;
        mc.player.abilities.setFlySpeed(prevFlySpeed);

        mc.player.refreshPositionAndAngles(playerPos[0], playerPos[1], playerPos[2], playerRot[0], playerRot[1]);
        mc.player.setVelocity(Vec3d.ZERO);

        if (riding != null && mc.world.getEntityById(riding.getEntityId()) != null) {
            mc.player.startRiding(riding);
        }

        super.onDisable();
    }

    @Subscribe
    public void sendPacket(SendPacketEvent event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket || event.getPacket() instanceof PlayerMoveC2SPacket) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onOpenScreen(OpenScreenEvent event) {
        if (riding instanceof HorseBaseEntity) {
            if (event.getScreen() instanceof InventoryScreen) {
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.OPEN_INVENTORY));
                event.setCancelled(true);
            }
        }
    }

    @Subscribe
    public void onMove(OnMoveEvent event) {
        mc.player.noClip = true;
    }

    @Subscribe
    public void onTick(TickEvent event) {
        mc.player.setOnGround(false);
        mc.player.abilities.setFlySpeed((float) (getSetting(0).asSlider().getValue() / 5));
        mc.player.abilities.flying = true;
    }

}
