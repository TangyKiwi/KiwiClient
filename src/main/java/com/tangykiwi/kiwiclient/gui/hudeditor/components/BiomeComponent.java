package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class BiomeComponent extends HUDComponent {
    public BiomeComponent(float x, float y) {
        super("Biome", x, y);
    }

    @Override
    public void render(DrawContext context, FontRenderer fontRenderer) {
        fontRenderer.drawString(context, "Biome: " + getBiome(), getX(), getY(), 0xFFAA00);
    }

    private String getBiome() {
        return mc.world.getRegistryManager().getOptional(RegistryKeys.BIOME)
            .map(biomeRegistry -> {
                Identifier id = biomeRegistry.getId(mc.world.getBiome(new BlockPos.Mutable().set(mc.player.getX(), mc.player.getY(), mc.player.getZ())).value());
                if (id == null) return "Unknown";
                return Arrays.stream(id.getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
            })
            .orElse("Unknown");
    }
}
