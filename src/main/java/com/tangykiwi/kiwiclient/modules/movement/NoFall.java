package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.PlayerMoveC2SPacketAccessor;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", "Prevents fall damage", KEY_UNBOUND, Category.MOVEMENT,
            new ModeSetting("Mode", "Simple", "Packet"));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent e) {
        if (mc.player != null && mc.player.fallDistance > 2.5f && getSetting(0).asMode().mode == 0) {
            if (mc.player.isFallFlying()) return;
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPacketSend(SendPacketEvent event) {
        if(getSetting(0).asMode().mode == 1 && event.packet instanceof PlayerMoveC2SPacket) {
            if(KiwiClient.moduleManager.getModule(Fly.class).isEnabled()) {
                if (mc.player.isFallFlying()) return;
                if (mc.player.getVelocity().y > -0.5) return;
                ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
            } else {
                ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
            }
        }
    }
}
