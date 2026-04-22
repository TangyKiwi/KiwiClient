package com.tangykiwi.kiwiclient.module.player;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import com.tangykiwi.kiwiclient.event.SendMovementPacketEvent;
import com.tangykiwi.kiwiclient.mixin.ServerboundMovePlayerPacketAccessor;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class AntiHunger extends Module {
    private boolean lastOnGround;
    private boolean ignorePacket;

    public AntiHunger() {
        super("AntiHunger", "Reduces hunger consumption", KEY_UNBOUND, Category.PLAYER,
            new ToggleSetting("Spoof Sprint", "Spoofs sprinting packets", true),
            new ToggleSetting("Spoof OnGround", "Spoofs OnGround flag", true));
    }

    @Override
    public void onEnable() {
        if (mc.player == null) {
            lastOnGround = true;
        } else {
            lastOnGround = mc.player.onGround();
        }

        super.onEnable();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onSendPacket(PacketEvent.Send event) {
        if (mc.player == null) return;
        
        if (ignorePacket && event.packet instanceof ServerboundMovePlayerPacket) {
            ignorePacket = false;
            return;
        }

        if (mc.player.isPassenger() || mc.player.isInWater() || mc.player.isUnderWater()) return;

        if (event.packet instanceof ServerboundPlayerCommandPacket packet && getSetting(0).asToggle().getValue()) {
            if (packet.getAction() == ServerboundPlayerCommandPacket.Action.START_SPRINTING) {
                event.cancel();
            }
        }

        if (event.packet instanceof ServerboundMovePlayerPacket packet && getSetting(1).asToggle().getValue() && mc.player.onGround() && mc.player.fallDistance <= 0.0 && !mc.gameMode.isDestroying()) {
            ((ServerboundMovePlayerPacketAccessor) packet).setOnGround(false);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onSendMovementPacketsHead(SendMovementPacketEvent.Pre event) {
        if (mc.player == null) {
            return;
        }

        if (mc.player.onGround() && !lastOnGround && getSetting(1).asToggle().getValue()) {
            ignorePacket = true;
        }

        lastOnGround = mc.player.onGround();
    }
}
