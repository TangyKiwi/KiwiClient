package com.tangykiwi.kiwiclient.util.render;

import java.util.OptionalDouble;
import java.util.function.Function;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.util.Util;

public class CustomRenderLayers {
    public static final RenderLayer QUADS;
    public static final Function<Double, RenderLayer> LINES;

    static {
        QUADS = RenderLayer.of("kiwiclient_layer_quads", 1536, CustomRenderPipelines.QUADS, RenderLayer.MultiPhaseParameters.builder()
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.ITEM_ENTITY_TARGET)
                .build(false));
        LINES = Util.memoize(lineWidth -> RenderLayer.of("kiwiclient_layer_lines", 1536, CustomRenderPipelines.LINES, RenderLayer.MultiPhaseParameters.builder()
                .lineWidth(new LineWidth(lineWidth == 0d ? OptionalDouble.empty() : OptionalDouble.of(lineWidth)))
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.ITEM_ENTITY_TARGET)
                .build(false)));
    }
}
