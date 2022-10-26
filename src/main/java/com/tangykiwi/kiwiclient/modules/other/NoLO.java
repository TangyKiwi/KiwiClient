package com.tangykiwi.kiwiclient.modules.other;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.OpenScreenEvent;
import com.tangykiwi.kiwiclient.event.ReceivePacketEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;

public class NoLO extends Module {
    public NoLO() {
        super("NoLO", "Disables the funky stuff on LiveOverflow's server", KEY_UNBOUND, Category.OTHER);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onOpenScreen(OpenScreenEvent event) {
        if(event.getScreen() instanceof DemoScreen) {
            event.setCancelled(true);
            System.out.println("Cancelled DemoScreen");
        } else if(event.getScreen() instanceof CreditsScreen) {
            event.setCancelled(true);
            System.out.println("Cancelled credits screen");
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onReceivePacket(ReceivePacketEvent event) {
        if(event.getPacket() instanceof GameStateChangeS2CPacket) {
            GameStateChangeS2CPacket packet = (GameStateChangeS2CPacket) event.getPacket();
            if(packet.getReason().equals(GameStateChangeS2CPacket.DEMO_MESSAGE_SHOWN) ||
                packet.getValue() == 104) {
                System.out.println("Cancelled demo packet");
                event.setCancelled(true);
            } else if(packet.getReason().equals(GameStateChangeS2CPacket.GAME_MODE_CHANGED)) {
                System.out.println("Cancelled gmc packet");
                event.setCancelled(true);
            }
        }
        if(event.getPacket() instanceof WorldBorderInitializeS2CPacket) {
            System.out.println("Cancelled world border packet");
            event.setCancelled(true);
        }
    }
}
