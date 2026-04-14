package com.tangykiwi.kiwiclient.util.render.state;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tangykiwi.kiwiclient.util.render.CustomRenderPipelines;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;

public record CustomRoundedQuadRenderState(RenderPipeline pipeline,
    TextureSetup textureSetup, Matrix3x2f pose, float x1, float y1, float x2,
    float y2, float rad, float samples, int color, @Nullable ScreenRectangle scissorArea,
	@Nullable ScreenRectangle bounds) implements GuiElementRenderState {

    public CustomRoundedQuadRenderState(Matrix3x2f pose, float x1, float y1, float x2,
        float y2, float rad, float samples, int color, @Nullable ScreenRectangle scissorArea) {
        this(CustomRenderPipelines.GUI_TRIANGLE_FAN, TextureSetup.noTexture(), pose, x1, y1, x2, y2, rad, samples, color,
        scissorArea, createBounds(x1, y1, x2, y2, pose, scissorArea));
    }

    public CustomRoundedQuadRenderState(GuiGraphicsExtractor context, float x1, float y1, float x2,
        float y2, float rad, float samples, int color) {
        this(CustomRenderPipelines.GUI_TRIANGLE_FAN, TextureSetup.noTexture(), context.pose(), x1, y1, x2, y2, rad, samples, color,
            context.scissorStack.peek(), createBounds(x1, y1, x2, y2, context.pose(), context.scissorStack.peek()));
    }

    @Override
    public void buildVertices(VertexConsumer vertices) {
        double[][] map = new double[][] { new double[] { x2 - rad, y2 - rad, rad }, new double[] { x2 - rad, y1 + rad, rad },
                new double[] { x1 + rad, y1 + rad, rad }, new double[] { x1 + rad, y2 - rad, rad } };
        for (int i = 0; i < 4; i++) {
            double[] current = map[i];
            double rad = current[2];
            for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
                float rad1 = (float) Math.toRadians(r);
                float rad2 = (float) Math.toRadians(Math.min(360 / 4d + i * 90d, r + 90 / samples));
                float sin = (float) (Math.sin(rad1) * rad);
                float cos = (float) (Math.cos(rad1) * rad);
                float sin2 = (float) (Math.sin(rad2) * rad);
                float cos2 = (float) (Math.cos(rad2) * rad);
                vertices.addVertexWith2DPose(pose(), x1 + (x2 - x1) / 2f, y1 + (y2 - y1) / 2f).setColor(color());
                vertices.addVertexWith2DPose(pose(), (float) current[0] + sin, (float) current[1] + cos).setColor(color());
                vertices.addVertexWith2DPose(pose(), (float) current[0] + sin2, (float) current[1] + cos2).setColor(color());
            }
            float rad1 = (float) Math.toRadians((360 / 4d + i * 90d));
            float sin = (float) (Math.sin(rad1) * rad);
            float cos = (float) (Math.cos(rad1) * rad);
            vertices.addVertexWith2DPose(pose(), x1 + (x2 - x1) / 2f, y1 + (y2 - y1) / 2f).setColor(color());
            vertices.addVertexWith2DPose(pose(), (float) current[0] + sin, (float) current[1] + cos).setColor(color());
            vertices.addVertexWith2DPose(pose(), (float) map[(i + 1) % 4][0] + sin, (float) map[(i + 1) % 4][1] + cos).setColor(color());
        }
    }
    
    @Nullable
    private static ScreenRectangle createBounds(float x1, float y1, float x2,
        float y2, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        float minX = Math.min(x1, x2);
		float maxX = Math.max(x1, x2);
		float minY = Math.min(y1, y2);
		float maxY = Math.max(y1, y2);
		
		ScreenRectangle screenRect = new ScreenRectangle((int)minX, (int)minY,
			(int)(maxX - minX), (int)(maxY - minY)).transformMaxBounds(pose);
		return scissorArea != null ? scissorArea.intersection(screenRect)
			: screenRect;
    }
}
