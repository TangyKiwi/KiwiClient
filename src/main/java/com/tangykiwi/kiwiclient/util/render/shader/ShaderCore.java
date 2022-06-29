package com.tangykiwi.kiwiclient.util.render.shader;

import java.io.IOException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ShaderCore {

    private static final Shader COLOR_OVERLAY_SHADER;

    public static Shader getColorOverlayShader() {
        return COLOR_OVERLAY_SHADER;
    }

    static {
        try {
            COLOR_OVERLAY_SHADER = ShaderLoader.load(VertexFormats.POSITION_COLOR_TEXTURE, new Identifier("kiwiclient", "color_overlay"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initilize KiwiClient core shaders");
        }
    }
}