package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.phys.Vec3;

public class NetherCoordsComponent extends HUDComponent {
    public NetherCoordsComponent(float x, float y) {
        super("Nether Coords", x, y);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);

        String renderString = "(Nether) X: 0.0 Y: 0.0 Z: 0.0";

        if (mc.player == null || mc.level == null) {
            setWidth((int) fontRenderer.getStringWidth(renderString));
            fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
            return;
        }

        Boolean nether = mc.level.dimension().identifier().getPath().contains("nether");
        Vec3 vec2 = mc.player.position();
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
