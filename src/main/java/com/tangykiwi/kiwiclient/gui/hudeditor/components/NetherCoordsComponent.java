package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

public class NetherCoordsComponent extends HUDComponent {
    public NetherCoordsComponent(float x, float y) {
        super("Nether Coords", x, y);
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        String renderString = "(Nether) X: 0.0 Y: 0.0 Z: 0.0";

        if (mc.player == null || mc.world == null) {
            setWidth((int) fontRenderer.getStringWidth(renderString));
            fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
            return;
        }

        Boolean nether = mc.world.getRegistryKey().getValue().getPath().contains("nether");
        Vec3d vec2 = mc.player.getPos();
        double altx = vec2.x / 8;
        double altz = vec2.z / 8;

        if (nether) {
            altx = vec2.x * 8;
            altz = vec2.z * 8;
        }
        if (nether) renderString = String.format("(Overworld) X: %.1f Y: %.1f Z: %.1f", altx, vec2.y, altz);
        else renderString = String.format("(Nether) X: %.1f Y: %.1f Z: %.1f", altx, vec2.y, altz);
        setWidth((int) fontRenderer.getStringWidth(renderString));
        fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
    }
}
