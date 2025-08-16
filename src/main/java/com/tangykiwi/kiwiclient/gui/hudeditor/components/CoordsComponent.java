package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CoordsComponent extends HUDComponent {
    public CoordsComponent(float x, float y) {
        super("Coords", x, y);
    }

    @Override
    public void render(DrawContext context, FontRenderer fontRenderer) {
        if (mc.player == null || mc.world == null) {
            fontRenderer.drawString(context, String.format("X: 0.0 Y: 0.0 Z: 0.0", getX(), getY()), getX(), getY(), 0xFFAA00);
            return;
        }
        
        Vec3d vec = mc.player.getPos();
        float yaw = MathHelper.wrapDegrees(mc.getCameraEntity().getYaw());
        String dir = "";
        if(yaw > 157.5) dir = "N -Z";
        else if(yaw >= 112.5) dir = "NW -X, -Z";
        else if(yaw > 67.5) dir = "W -X";
        else if(yaw >= 22.5) dir = "SW -X, +Z";
        else if(yaw > -22.5) dir = "S +Z";
        else if(yaw >= -67.5) dir = "SE +X, +Z";
        else if(yaw > -112.5) dir = "E +X";
        else if(yaw >= -157.5) dir = "NE +X, -Z";
        else dir = "N -Z";

        fontRenderer.drawString(context, String.format("X: %.1f Y: %.1f Z: %.1f " + dir, vec.x, vec.y, vec.z), getX(), getY(), 0xFFAA00);
    }
    
}
