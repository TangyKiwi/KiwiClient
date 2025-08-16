package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class PingComponent extends HUDComponent {
    public PingComponent(float x, float y) {
        super("Ping", x, y);
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        String renderString = "Ping: 0";
        if (mc.player == null || mc.world == null) {
            setWidth((int) fontRenderer.getStringWidth(renderString));
            fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
            return;
        }

        PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
        int ping = playerEntry == null ? 0 : playerEntry.getLatency();
        renderString = String.format("Ping: %d", ping);
        setWidth((int) fontRenderer.getStringWidth(renderString));
        fontRenderer.drawString(context, renderString, getX(), getY(), getColorString(ping, 10, 20, 50, 75, 100, true));
    }
}
