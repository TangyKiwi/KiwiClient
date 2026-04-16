package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PingComponent extends HUDComponent {
    public PingComponent(float x, float y) {
        super("Ping", x, y);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);

        String renderString = "Ping: 0";
        if (mc.player == null || mc.level == null) {
            setWidth((int) fontRenderer.getStringWidth(renderString));
            fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
            return;
        }

        PlayerInfo playerEntry = mc.player.connection.getPlayerInfo(mc.player.getUUID());
        int ping = playerEntry == null ? 0 : playerEntry.getLatency();
        renderString = String.format("Ping: %d", ping);
        setWidth((int) fontRenderer.getStringWidth(renderString));
        fontRenderer.drawString(context, renderString, getX(), getY(), getColorString(ping, 10, 20, 50, 75, 100, true));
    }
}
