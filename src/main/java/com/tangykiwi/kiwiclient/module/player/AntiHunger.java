package com.tangykiwi.kiwiclient.module.player;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import com.tangykiwi.kiwiclient.event.SendMovementPacketEvent;
import com.tangykiwi.kiwiclient.mixin.PlayerMoveC2SPacketAccessor;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

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
        lastOnGround = mc.player.isOnGround();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onSendPacket(PacketEvent.Send event) {
        if (mc.player == null) {
            return;
        }
        
        if (ignorePacket && event.packet instanceof PlayerMoveC2SPacket) {
            ignorePacket = false;
            return;
        }

        if (mc.player.hasVehicle() || mc.player.isSubmergedInWater() || mc.player.isTouchingWater()) return;

        if (event.packet instanceof ClientCommandC2SPacket packet && getSetting(0).asToggle().getValue()) {
            if (packet.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                event.cancel();
            }
        }

        if (event.packet instanceof PlayerMoveC2SPacket packet && getSetting(1).asToggle().getValue() && mc.player.isOnGround() && mc.player.fallDistance <= 0.0 && !mc.interactionManager.isBreakingBlock()) {
            ((PlayerMoveC2SPacketAccessor) packet).setOnGround(false);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onSendMovementPacketsHead(SendMovementPacketEvent.Pre event) {
        if (mc.player == null) {
            return;
        }

        if (mc.player.isOnGround() && !lastOnGround && getSetting(1).asToggle().getValue()) {
            ignorePacket = true;
        }

        lastOnGround = mc.player.isOnGround();
    }
}
