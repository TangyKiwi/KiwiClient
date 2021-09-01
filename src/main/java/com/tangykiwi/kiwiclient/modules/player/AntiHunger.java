package com.tangykiwi.kiwiclient.modules.player;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.PlayerMoveC2SPacketAccessor;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHunger extends Module {
    private boolean lastOnGround;
    private boolean sendOnGroundTruePacket;
    private boolean ignorePacket;

    public AntiHunger() {
        super("AntiHunger", "Reduces hunger consumption", KEY_UNBOUND, Category.PLAYER,
            new ToggleSetting("Spoof Sprint", true).withDesc("Spoofs sprinting packets"),
            new ToggleSetting("Spoon OnGround", true).withDesc("Spoofs OnGround flag"));
    }

    @Override
    public void onEnable() {
        super.onEnable();

        lastOnGround = mc.player.isOnGround();
        sendOnGroundTruePacket = true;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onSendPacket(SendPacketEvent event) {
        if (ignorePacket) return;

        if (event.getPacket() instanceof ClientCommandC2SPacket && getSetting(0).asToggle().state) {
            ClientCommandC2SPacket.Mode mode = ((ClientCommandC2SPacket) event.getPacket()).getMode();

            if (mode == ClientCommandC2SPacket.Mode.START_SPRINTING || mode == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                event.setCancelled(true);
            }
        }

        if (event.getPacket() instanceof PlayerMoveC2SPacket && getSetting(1).asToggle().state && mc.player.isOnGround() && mc.player.fallDistance <= 0.0 && !mc.interactionManager.isBreakingBlock()) {
            ((PlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(false);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent event) {
        if (mc.player.isOnGround() && !lastOnGround && !sendOnGroundTruePacket) sendOnGroundTruePacket = true;

        if (mc.player.isOnGround() && sendOnGroundTruePacket && getSetting(1).asToggle().state) {
            ignorePacket = true;
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
            ignorePacket = false;

            sendOnGroundTruePacket = false;
        }

        lastOnGround = mc.player.isOnGround();
    }
}
