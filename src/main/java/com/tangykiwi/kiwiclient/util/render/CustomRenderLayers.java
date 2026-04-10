package com.tangykiwi.kiwiclient.util.render;

import java.util.OptionalDouble;
import java.util.function.Function;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderSetup.OutlineProperty;
import net.minecraft.network.chat.ClickEvent.Custom;
import net.minecraft.util.Util;

public class CustomRenderLayers {
    public static final RenderType QUADS;
    public static final Function<Double, RenderType> LINES;

    static {
        QUADS = RenderType.create("kiwiclient_layer_quads", RenderSetup.builder(CustomRenderPipelines.QUADS)
                .bufferSize(1536)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .createRenderSetup());
        LINES = Util.memoize(lineWidth -> RenderType.create("kiwiclient_layer_lines", RenderSetup.builder(CustomRenderPipelines.LINES)
                .bufferSize(1536)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .setLineWidth(lineWidth == 0d ? OptionalDouble.empty() : OptionalDouble.of(lineWidth))
                .createRenderSetup()));
    }
}
