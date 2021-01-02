package com.tangykiwi.kiwiclient.modules.combat;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module {

    public Criticals() {
        super("Criticals", "Forces critical hits", KEY_UNBOUND, Category.COMBAT);
    }

    @Subscribe
    public void sendPacket(SendPacketEvent event) {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.getPacket();
            if (packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) this.doCritical();
        }
    }

    public void doCritical() {
        if (mc.player.isInLava() || mc.player.isTouchingWater()) return;
        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(posX, posY + 0.0625, posZ, true));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(posX, posY, posZ, false));
    }
}
