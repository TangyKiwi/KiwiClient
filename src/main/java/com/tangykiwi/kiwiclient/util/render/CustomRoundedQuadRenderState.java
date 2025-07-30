package com.tangykiwi.kiwiclient.util.render;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;

public record CustomRoundedQuadRenderState(RenderPipeline pipeline,
    TextureSetup textureSetup, Matrix3x2f pose, float x1, float y1, float x2,
    float y2, float rad, float samples, int color, @Nullable ScreenRect scissorArea,
	@Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {

    public CustomRoundedQuadRenderState(Matrix3x2f pose, float x1, float y1, float x2,
        float y2, float rad, float samples, int color, @Nullable ScreenRect scissorArea) {
        this(RenderPipelines.GUI, TextureSetup.empty(), pose, x1, y1, x2, y2, rad, samples, color,
        scissorArea, createBounds(x1, y1, x2, y2, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices, float depth) {
        double[][] map = new double[][] { new double[] { x2 - rad, y2 - rad, rad }, new double[] { x2 - rad, y1 + rad, rad },
                new double[] { x1 + rad, y1 + rad, rad }, new double[] { x1 + rad, y2 - rad, rad } };
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                vertices.vertex(pose(), (float) current[0] + sin, (float) current[1] + cos, depth).color(color());
            }
            float rad1 = (float) Math.toRadians((360 / 4d + i * 90d));
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            vertices.vertex(pose(), (float) current[0] + sin, (float) current[1] + cos, depth).color(color());
        }
    }
    
    @Nullable
    private static ScreenRect createBounds(float x1, float y1, float x2,
        float y2, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        float minX = Math.min(x1, x2);
		float maxX = Math.max(x1, x2);
		float minY = Math.min(y1, y2);
		float maxY = Math.max(y1, y2);
		
		ScreenRect screenRect = new ScreenRect((int)minX, (int)minY,
			(int)(maxX - minX), (int)(maxY - minY)).transformEachVertex(pose);
		return scissorArea != null ? scissorArea.intersection(screenRect)
			: screenRect;
    }
}
