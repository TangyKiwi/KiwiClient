package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.gui.DrawContext;

public class FPSComponent extends HUDComponent {
    public FPSComponent(float x, float y) {
        super("FPS", x, y);
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);
        
        int fps = (mc.fpsDebugString.equals("")) ? 0 : Integer.parseInt(mc.fpsDebugString.replaceAll("[^\\d]", " ").trim().replaceAll(" +", " ").split(" ")[0]);
        String renderString = String.format("FPS: %d", fps);
        setWidth((int) fontRenderer.getStringWidth(renderString));
        fontRenderer.drawString(context, renderString, getX(), getY(), getColorString(fps, 80, 60, 30, 15, 10, false));
    }
}
