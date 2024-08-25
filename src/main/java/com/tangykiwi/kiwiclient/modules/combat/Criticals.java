package com.tangykiwi.kiwiclient.modules.combat;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import com.tangykiwi.kiwiclient.mixininterface.IPlayerInteractEntityC2SPacket;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.movement.NoFall;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Criticals extends Module {

    public Criticals() {
        super("Criticals", "Forces critical hits", KEY_UNBOUND, Category.COMBAT);
    }

    @Subscribe
    public void sendPacket(SendPacketEvent event) {
        if (event.getPacket() instanceof IPlayerInteractEntityC2SPacket) {
            IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket) event.getPacket();
            if (packet.getType() == PlayerInteractEntityC2SPacket.InteractType.ATTACK
                    && packet.getEntity() instanceof LivingEntity) {
                doCritical();
            }
        }
    }

    public void doCritical() {
        KiwiClient.moduleManager.getModule(NoFall.class).setToggled(false);

        if (mc.player.isInLava() || mc.player.isTouchingWater()) return;

        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(posX, posY + 0.0633, posZ, false));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(posX, posY, posZ, false));

        KiwiClient.moduleManager.getModule(NoFall.class).setToggled(true);
    }
}
