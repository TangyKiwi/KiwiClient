package com.tangykiwi.kiwiclient.util.render.shader;

import java.io.IOException;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ShaderCore {

    private static final ShaderProgram COLOR_OVERLAY_SHADER;

    public static ShaderProgram getColorOverlayShader() {
        return COLOR_OVERLAY_SHADER;
    }

    static {
        try {
            COLOR_OVERLAY_SHADER = ShaderLoader.load(VertexFormats.POSITION_TEXTURE_COLOR, Identifier.of("kiwiclient", "color_overlay"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize KiwiClient core shaders");
        }
    }
}