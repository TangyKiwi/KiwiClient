package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import java.util.Map;
import java.util.UUID;
import java.awt.Color;

import com.mojang.datafixers.util.Either;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.client.HUD;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3i;

public class PlayerWaypointsComponent extends HUDComponent {
    public PlayerWaypointsComponent(float x, float y) {
        super("Waypoints", x, y);
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);
        
        float curY = getY();
        Map<Either<UUID, String>, Vec3i> waypoints = ((HUD) KiwiClient.moduleManager.getModule(HUD.class)).waypoints;
        // KiwiClient.LOGGER.info(waypoints.size() + " waypoints stored");
        for (Map.Entry<Either<UUID, String>, Vec3i> entry : waypoints.entrySet()) {
            Either<UUID, String> source = entry.getKey();
            Vec3i pos = entry.getValue();
            String label = source.map(
                uuid -> uuid.toString(),
                name -> name
            );
            setWidth((int) fontRenderer.getStringWidth(label));
            String coords = "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ();
            fontRenderer.drawString(context, label, getX(), curY, Color.WHITE.getRGB());
            fontRenderer.drawString(context, coords, getX(), curY + fontHeight, Color.LIGHT_GRAY.getRGB());
            curY += fontHeight * 2;
        }
    }
}
