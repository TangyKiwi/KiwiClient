package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.gui.DrawContext;

public class SpeedComponent extends HUDComponent {
    public SpeedComponent(float x, float y) {
        super("Speed", x, y);
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        String renderString = "Speed: 0.0 b/s";
        if (mc.player == null || mc.world == null) {
            setWidth((int) fontRenderer.getStringWidth(renderString));
            fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
            return;
        }

        renderString = String.format("Speed: %.1f b/s", getSpeed());
        setWidth((int) fontRenderer.getStringWidth(renderString));
        fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
    }
    
    public static double getSpeed() {
        double tX = Math.abs(mc.player.getX() - mc.player.lastX);
        double tY = Math.abs(mc.player.getY() - mc.player.lastY);
        double tZ = Math.abs(mc.player.getZ() - mc.player.lastZ);
        double length = Math.sqrt(tX * tX + tY * tY + tZ * tZ);

        double bps = length * 20;
        return bps;
    }
}
