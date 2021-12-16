package com.tangykiwi.kiwiclient.util.shader;

import java.io.IOException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;

public class ShaderCore {

    private static final Shader COLOR_OVERLAY_SHADER;

    public static Shader getColorOverlayShader() {
        return COLOR_OVERLAY_SHADER;
    }

    static {
        try {
            COLOR_OVERLAY_SHADER = new Shader(MinecraftClient.getInstance().getResourceManager(), "kiwiclient:color_overlay", VertexFormats.POSITION_COLOR_TEXTURE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initilize KiwiClient core shaders");
        }
    }

}