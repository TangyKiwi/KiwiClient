package com.tangykiwi.kiwiclient.util.render;

import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class CustomRenderLayers {
    public static final RenderType QUADS;
    public static final RenderType LINES;

    static {
        QUADS = RenderType.create("kiwiclient_layer_quads", RenderSetup.builder(CustomRenderPipelines.QUADS)
                .bufferSize(1536)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .createRenderSetup());
        LINES = RenderType.create("kiwiclient_layer_lines", RenderSetup.builder(CustomRenderPipelines.LINES)
                .bufferSize(1536)
                .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                .createRenderSetup());
    }
}
