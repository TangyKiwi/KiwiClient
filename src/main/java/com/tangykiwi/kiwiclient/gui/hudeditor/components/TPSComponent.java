package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;

public class TPSComponent extends HUDComponent {
    public TPSComponent(float x, float y) {
        super("TPS", x, y);
    }

    @Override
    public void render(DrawContext context, FontRenderer fontRenderer) {
        float tps = 20; // placeholder
        fontRenderer.drawString(context, "TPS: " + String.format("%.1f", tps), getX(), getY(), getColorString((int) tps, 20, 18, 16, 14, 12, false));
    }
}
