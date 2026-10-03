package com.tangykiwi.kiwiclient.util.render;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.ShaderSource;
import com.mojang.renderpearl.api.pipeline.UniformType;
import com.tangykiwi.kiwiclient.KiwiClient;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class CustomRenderPipelines {
    private static final List<RenderPipeline> PIPELINES = new ArrayList<>();

    public static final RenderPipeline GUI_TRIANGLE_FAN = add(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
        .withLocation(Identifier.fromNamespaceAndPath("kiwiclient", "pipeline/gui_triangle_fan"))
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
        .withPrimitiveTopology(PrimitiveTopology.TRIANGLE_FAN)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withCull(false)
        .build());

    public static final RenderPipeline QUADS = add(RenderPipeline.builder()
        .withLocation(Identifier.fromNamespaceAndPath("kiwiclient", "pipeline/quads"))
        .withVertexShader("core/position_color").withFragmentShader("core/position_color")
        .withBindGroupLayout(BindGroupLayout.builder()
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .build())
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
        .withPrimitiveTopology(PrimitiveTopology.QUADS)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withCull(false)
        .build());

    public static final RenderPipeline LINES = add(RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
        .withLocation(Identifier.fromNamespaceAndPath("kiwiclient", "pipeline/lines"))
        .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH)
        .withPrimitiveTopology(PrimitiveTopology.LINES)
        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
        .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
        .withCull(false)
        .build());

    private static RenderPipeline add(RenderPipeline pipeline) {
        PIPELINES.add(pipeline);
        return pipeline;
    }

    public static void precompile() {
        for (RenderPipeline pipeline : PIPELINES) {
            RenderSystem.getCompiledPipeline(pipeline);
        }
    }
}
