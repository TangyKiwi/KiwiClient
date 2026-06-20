package com.tangykiwi.kiwiclient.module.combat;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import com.tangykiwi.kiwiclient.mixininterface.IServerboundMovePlayerPacket;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", "Forces critical hits", KEY_UNBOUND, Category.COMBAT);
    }

    @Subscribe
    public void onSendPacket(PacketEvent.Send event) {
        if (event.packet instanceof ServerboundAttackPacket(int entityId)) {
            Entity entity = mc.level.getEntity(entityId);
            
            doCritical(entity);
        }
    }

    public void doCritical(Entity entity) {
        if (!(entity instanceof LivingEntity) || !  mc.player.onGround() || mc.player.isInWater() || mc.player.isInLava() || mc.player.onClimbable()) return;

        sendPacket(0.0625);
        sendPacket(0);
    }

    private void sendPacket(double height) {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        ServerboundMovePlayerPacket packet = new ServerboundMovePlayerPacket.Pos(x, y + height, z, false, false);
        ((IServerboundMovePlayerPacket) packet).setTag(1337);
        mc.player.connection.send(packet);
    }
}
