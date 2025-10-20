package com.tangykiwi.kiwiclient.util.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

public class CustomRenderLayers {
    public static final RenderLayer LINES;

    private static RenderLayer.MultiPhaseParameters emptyParams() {
        return RenderLayer.MultiPhaseParameters.builder()
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.ITEM_ENTITY_TARGET)
                .build(false);
    }

    static {
        LINES = RenderLayer.of("kiwiclient_layer_lines", 256, CustomRenderPipelines.LINES, emptyParams());
    }
}
