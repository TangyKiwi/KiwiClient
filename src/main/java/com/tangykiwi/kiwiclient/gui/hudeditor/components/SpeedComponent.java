package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;

public class SpeedComponent extends HUDComponent {
    public SpeedComponent(float x, float y) {
        super("Speed", x, y);
    }

    @Override
    public void render(DrawContext context, FontRenderer fontRenderer) {
        if (mc.player == null || mc.world == null) {
            fontRenderer.drawString(context, String.format("Speed: 0.0 b/s", getX(), getY()), getX(), getY(), 0xFFAA00);
            return;
        }
        
        double tX = Math.abs(mc.player.getX() - mc.player.lastX);
        double tY = Math.abs(mc.player.getY() - mc.player.lastY);
        double tZ = Math.abs(mc.player.getZ() - mc.player.lastZ);
        double length = Math.sqrt(tX * tX + tY * tY + tZ * tZ);

        double bps = length * 20;
        fontRenderer.drawString(context, String.format("Speed: %.1f b/s", bps), getX(), getY(), 0xFFAA00);
    }
    
}
