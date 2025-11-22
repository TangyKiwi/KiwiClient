package com.tangykiwi.kiwiclient.module.movement;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.PlayerMoveC2SPacketAccessor;
import com.tangykiwi.kiwiclient.mixininterface.IPlayerMoveC2SPacket;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ModeSetting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", "Prevents fall damage", Category.MOVEMENT,
            new ModeSetting("Mode", "NoFall mode", "Simple", "Packet"));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent.Pre e) {
        if (mc.player == null) return;
        if (mc.player.getAbilities().creativeMode) return;
        if (getSetting(0).asMode().getValue() == 0) {
            if (mc.player.isGliding()) return;
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, mc.player.horizontalCollision));
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPacketSend(PacketEvent.Send event) {
        if(getSetting(0).asMode().getValue() == 0 || !(event.packet instanceof PlayerMoveC2SPacket) || ((IPlayerMoveC2SPacket) event.packet).getTag() == 1337) {
            return;
        }
        if(getSetting(0).asMode().getValue() == 1) {
            if(!KiwiClient.moduleManager.getModule(Fly.class).isEnabled()) {
                if (mc.player.isGliding()) return;
                if (mc.player.getVelocity().y > -0.5) return;
                ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
            } else {
                ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
            }
        }
    }
}