package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;

public class FPSComponent extends HUDComponent {
    public FPSComponent(float x, float y) {
        super("FPS", x, y);
    }

    @Override
    public void render(DrawContext context, FontRenderer fontRenderer) {
        int fps = (mc.fpsDebugString.equals("")) ? 0 : Integer.parseInt(mc.fpsDebugString.replaceAll("[^\\d]", " ").trim().replaceAll(" +", " ").split(" ")[0]);
        fontRenderer.drawString(context, String.format("FPS: %d", fps), getX(), getY(), getColorString(fps, 80, 60, 30, 15, 10, false));
    }
}
