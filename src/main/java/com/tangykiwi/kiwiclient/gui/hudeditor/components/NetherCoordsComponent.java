package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

public class NetherCoordsComponent extends HUDComponent {
    public NetherCoordsComponent(float x, float y) {
        super("Nether Coords", x, y);
    }

    @Override
    public void render(DrawContext context, FontRenderer fontRenderer) {
        Boolean nether = mc.world.getRegistryKey().getValue().getPath().contains("nether");
        Vec3d vec2 = mc.player.getPos();
        double altx = vec2.x / 8;
        double altz = vec2.z / 8;

        if (nether) {
            altx = vec2.x * 8;
            altz = vec2.z * 8;
        }
        if (nether) fontRenderer.drawString(context, String.format("(Overworld) X: %.1f Y: %.1f Z: %.1f", altx, vec2.y, altz), getX(), getY(), 0xFFAA00);
        else fontRenderer.drawString(context, String.format("(Nether) X: %.1f Y: %.1f Z: %.1f", altx, vec2.y, altz), getX(), getY(), 0xFFAA00);
    }
}
