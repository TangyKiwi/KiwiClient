package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import com.tangykiwi.kiwiclient.KiwiClient;

import net.minecraft.client.gui.DrawContext;

public class TPSComponent extends HUDComponent {
    public TPSComponent(float x, float y) {
        super("TPS", x, y);
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        float tps = KiwiClient.tickRate.getTickRate();
        String renderString = "TPS: " + String.format("%.1f", tps);
        setWidth((int) fontRenderer.getStringWidth(renderString));
        fontRenderer.drawString(context, renderString, getX(), getY(), getColorString((int) tps, 18, 18, 16, 14, 12, false));
    }
}
