package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

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
        
        return mc.level.getRegistryManager().getOptional(RegistryKeys.BIOME)
            .map(biomeRegistry -> {
                Identifier id = biomeRegistry.getId(mc.level.getBiome(new BlockPos.Mutable().set(mc.player.getX(), mc.player.getY(), mc.player.getZ())).value());
                if (id == null) return "Unknown";
                return Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
            })
            .orElse("Unknown");
    }
}
