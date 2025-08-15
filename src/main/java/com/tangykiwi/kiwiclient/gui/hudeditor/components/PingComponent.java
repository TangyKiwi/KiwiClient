package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class PingComponent extends HUDComponent {
    public PingComponent(float x, float y) {
        super("Ping", x, y);
    }

    @Override
    public void render(DrawContext context, FontRenderer fontRenderer) {
        PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
        int ping = playerEntry == null ? 0 : playerEntry.getLatency();
        fontRenderer.drawString(context, String.format("Ping: %d", ping), getX(), getY(), getColorString(ping, 10, 20, 50, 75, 100, true));
    }
}
