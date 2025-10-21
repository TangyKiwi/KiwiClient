package com.tangykiwi.kiwiclient.util.render;

import java.util.OptionalDouble;
import java.util.function.Function;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderPhase.LineWidth;
import net.minecraft.util.Util;

public class CustomRenderLayers {
    public static final Function<Double, RenderLayer> LINES;
    // public static final RenderLayer LINES;

    private static RenderLayer.MultiPhaseParameters emptyParams() {
        return RenderLayer.MultiPhaseParameters.builder()
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.ITEM_ENTITY_TARGET)
                .build(false);
    }

    static {
        // LINES = Util.memoize(width -> RenderLayer.of("kiwiclient_layer_lines", 256, false, true, CustomRenderPipelines.LINES, RenderLayer.MultiPhaseParameters.builder()
        //         .lineWidth(new LineWidth(width == 0d ? OptionalDouble.empty() : OptionalDouble.of(width)))
        //         .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
        //         .target(RenderPhase.ITEM_ENTITY_TARGET)
        //         .build(false)));
        LINES = Util.memoize(lineWidth -> RenderLayer.of("kiwiclient_layer_lines", 1536, CustomRenderPipelines.LINES, RenderLayer.MultiPhaseParameters.builder()
                .lineWidth(new LineWidth(lineWidth == 0d ? OptionalDouble.empty() : OptionalDouble.of(lineWidth)))
                .layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
                .target(RenderPhase.ITEM_ENTITY_TARGET)
                .build(false)));
    }
}
