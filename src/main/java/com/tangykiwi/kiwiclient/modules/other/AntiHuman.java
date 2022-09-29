package com.tangykiwi.kiwiclient.modules.other;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import com.tangykiwi.kiwiclient.mixin.PlayerMoveC2SPacketAccessor;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHuman extends Module {
    public AntiHuman() {
        super("AntiHuman", "Bypasses anti-human detection on the LiveOverflow server", KEY_UNBOUND, Category.OTHER);
    }

    @Subscribe
    public void onPacket(SendPacketEvent event) {
        if(event.packet instanceof PlayerMoveC2SPacket) {
            PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket) event.getPacket();
            double x = ((int) (packet.getX(mc.player.getX()) * 100)) / 100.0;
            double z = ((int) (packet.getZ(mc.player.getZ()) * 100)) / 100.0;
            ((PlayerMoveC2SPacketAccessor) event.packet).setX(x);
            ((PlayerMoveC2SPacketAccessor) event.packet).setZ(z);
            mc.player.setPos(x, packet.getY(mc.player.getY()), z);

            if(mc.player.hasVehicle()) {
                Entity vehicle = mc.player.getVehicle();
                double vx = ((int) (vehicle.getX() * 100)) / 100.0;
                double vz = ((int) (vehicle.getZ() * 100)) / 100.0;
                mc.player.getVehicle().setPos(vx, mc.player.getVehicle().getY(), vz);
            }
//            System.out.println(packet.getX(mc.player.getX()) + " " + packet.getZ(mc.player.getZ()) + " | " + x + " " + z + " | " + mc.player.getX() + " " + mc.player.getZ() + " | " + (x * 1000) % 10 + " " + (z * 1000) % 10);
        }
    }
}
