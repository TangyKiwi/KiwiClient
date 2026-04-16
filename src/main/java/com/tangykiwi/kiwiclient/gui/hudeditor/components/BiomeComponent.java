package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class BiomeComponent extends HUDComponent {
    public BiomeComponent(float x, float y) {
        super("Biome", x, y);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);

        String renderString = "Biome: " + getBiome();
        setWidth((int) fontRenderer.getStringWidth(renderString));
        fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
    }

    private String getBiome() {
        if (mc.level == null || mc.player == null) {
            return "Unknown";
        }
        
        return mc.level.registryAccess().lookup(Registries.BIOME)
            .map(biomeRegistry -> {
                Identifier id = biomeRegistry.getKey(mc.level.getBiome(new BlockPos((int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ())).value());
                if (id == null) return "Unknown";
                return Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
            })
            .orElse("Unknown");
    }
}
