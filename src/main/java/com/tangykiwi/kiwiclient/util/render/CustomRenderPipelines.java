package com.tangykiwi.kiwiclient.util.render;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.tangykiwi.kiwiclient.KiwiClient;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class CustomRenderPipelines {
    private static final List<RenderPipeline> PIPELINES = new ArrayList<>();

    public static final RenderPipeline GUI_TRIANGLE_FAN = add(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
        .withLocation(Identifier.of("kiwiclient", "pipeline/gui_triangle_fan"))
        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
        .withBlend(BlendFunction.TRANSLUCENT)
        .withCull(false)
        .withDepthWrite(false)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build());

    public static final RenderPipeline QUADS = add(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
        .withLocation(Identifier.of("kiwiclient", "pipeline/quads"))
        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
        .withBlend(BlendFunction.TRANSLUCENT)
        .withCull(false)
        .withDepthWrite(false)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build());

    public static final RenderPipeline LINES = add(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
        .withLocation(Identifier.of("kiwiclient", "pipeline/lines"))
        .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL, VertexFormat.DrawMode.LINES)
        .withBlend(BlendFunction.TRANSLUCENT)
        .withCull(false)
        .withDepthWrite(false)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .build());

    private static RenderPipeline add(RenderPipeline pipeline) {
        PIPELINES.add(pipeline);
        return pipeline;
    }

    public static void precompile() {
        GpuDevice device = RenderSystem.getDevice();
        ResourceManager resources = KiwiClient.mc.getResourceManager();

        for (RenderPipeline pipeline : PIPELINES) {
            device.precompilePipeline(pipeline, (identifier, shaderType) -> {
                var resource = resources.getResource(identifier).get();

                try (var in = resource.getInputStream()) {
                    return IOUtils.toString(in, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
