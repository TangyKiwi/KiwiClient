package com.tangykiwi.kiwiclient.module.movement;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.ServerboundMovePlayerPacketAccessor;
import com.tangykiwi.kiwiclient.mixininterface.IServerboundMovePlayerPacket;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ModeSetting;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT,
            new ModeSetting("Mode", "NoFall mode", "Simple", "Packet"));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent.Pre e) {
        if (mc.player == null) return;
        if (mc.player.getAbilities().instabuild) return;
        if (getSetting(0).asMode().getValue() == 0) {
            if (mc.player.isFallFlying()) return;
            mc.player.connection.send(new ServerboundMovePlayerPacket.StatusOnly(true, mc.player.horizontalCollision));
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPacketSend(PacketEvent.Send event) {
        if(getSetting(0).asMode().getValue() == 0 || !(event.packet instanceof ServerboundMovePlayerPacket) || ((IServerboundMovePlayerPacket) event.packet).getTag() == 1337) {
            return;
        }
        if(getSetting(0).asMode().getValue() == 1) {
            if(!KiwiClient.moduleManager.getModule(Fly.class).isEnabled()) {
                if (mc.player.isFallFlying()) return;
                if (mc.player.getDeltaMovement().y > -0.5) return;
                ((ServerboundMovePlayerPacketAccessor) event.packet).setOnGround(true);
            } else {
                ((ServerboundMovePlayerPacketAccessor) event.packet).setOnGround(true);
            }
        }
    }
}