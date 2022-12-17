package com.tangykiwi.kiwiclient.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.mixin.WorldRendererAccessor;
import com.tangykiwi.kiwiclient.util.render.color.CustomColor;
import com.tangykiwi.kiwiclient.util.Utils;
import com.tangykiwi.kiwiclient.util.font.IFont;
import com.tangykiwi.kiwiclient.util.render.color.LineColor;
import com.tangykiwi.kiwiclient.util.render.color.QuadColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;

public class RenderUtils {
	private static Field shaderLightField;

	public static void renderRoundedQuad(MatrixStack matrices, Color c, double fromX, double fromY, double toX, double toY, double radC1, double radC2, double radC3, double radC4, double samples) {
		int color = c.getRGB();
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		float f = ((float) (color >> 24 & 255) / 255.0F);
		float g = (float) (color >> 16 & 255) / 255.0F;
		float h = (float) (color >> 8 & 255) / 255.0F;
		float k = (float) (color & 255) / 255.0F;
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
		renderRoundedQuadInternal(matrix, g, h, k, f, fromX, fromY, toX, toY, radC1, radC2, radC3, radC4, samples);
		RenderSystem.enableCull();
		RenderSystem.disableBlend();
	}

	public static void renderRoundedQuad(MatrixStack stack, Color c, double x, double y, double x1, double y1, double rad, double samples) {
		renderRoundedQuad(stack, c, x, y, x1, y1, rad, rad, rad, rad, samples);
	}

	public static void renderRoundedQuadInternal(Matrix4f matrix, float cr, float cg, float cb, float ca, double fromX, double fromY, double toX, double toY, double radC1, double radC2, double radC3, double radC4, double samples) {
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);

		double[][] map = new double[][] { new double[] { toX - radC4, toY - radC4, radC4 }, new double[] { toX - radC2, fromY + radC2, radC2 },
				new double[] { fromX + radC1, fromY + radC1, radC1 }, new double[] { fromX + radC3, toY - radC3, radC3 } };
		for (int i = 0; i < 4; i++) {
			double[] current = map[i];
			double rad = current[2];
			for (double r = i * 90d; r < (360 / 4d + i * 90d); r += (90 / samples)) {
				float rad1 = (float) Math.toRadians(r);
				float sin = (float) (Math.sin(rad1) * rad);
				float cos = (float) (Math.cos(rad1) * rad);
				bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).next();
			}
			float rad1 = (float) Math.toRadians((360 / 4d + i * 90d));
			float sin = (float) (Math.sin(rad1) * rad);
			float cos = (float) (Math.cos(rad1) * rad);
			bufferBuilder.vertex(matrix, (float) current[0] + sin, (float) current[1] + cos, 0.0F).color(cr, cg, cb, ca).next();
		}
		BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
	}

	// -------------------- Fill + Outline Boxes --------------------

	public static void drawBoxBoth(BlockPos blockPos, QuadColor color, float lineWidth, Direction... excludeDirs) {
		drawBoxBoth(new Box(blockPos), color, lineWidth, excludeDirs);
	}

	public static void drawBoxBoth(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
		QuadColor outlineColor = color.clone();
		outlineColor.overwriteAlpha(255);

		drawBoxBoth(box, color, outlineColor, lineWidth, excludeDirs);
	}

	public static void drawBoxBoth(BlockPos blockPos, QuadColor fillColor, QuadColor outlineColor, float lineWidth, Direction... excludeDirs) {
		drawBoxBoth(new Box(blockPos), fillColor, outlineColor, lineWidth, excludeDirs);
	}

	public static void drawBoxBoth(Box box, QuadColor fillColor, QuadColor outlineColor, float lineWidth, Direction... excludeDirs) {
		drawBoxFill(box, fillColor, excludeDirs);
		drawBoxOutline(box, outlineColor, lineWidth, excludeDirs);
	}

	// -------------------- Frustum --------------------
	public static Frustum getFrustum() {
		return ((WorldRendererAccessor) MinecraftClient.getInstance().worldRenderer).getFrustum();
	}

	// -------------------- Fill Boxes --------------------

	public static void drawBoxFill(BlockPos blockPos, QuadColor color, Direction... excludeDirs) {
		drawBoxFill(new Box(blockPos), color, excludeDirs);
	}

	public static void drawBoxFill(Box box, QuadColor color, Direction... excludeDirs) {
		if (!getFrustum().isVisible(box)) {
			return;
		}

		setup();

		MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Fill
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);

		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		Vertexer.vertexBoxQuads(matrices, buffer, Boxes.moveToZero(box), color, excludeDirs);
		tessellator.draw();

		cleanup();
	}

	// -------------------- Outline Boxes --------------------

	public static void drawBoxOutline(BlockPos blockPos, QuadColor color, float lineWidth, Direction... excludeDirs) {
		drawBoxOutline(new Box(blockPos), color, lineWidth, excludeDirs);
	}

	public static void drawBoxOutline(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
		if (!getFrustum().isVisible(box)) {
			return;
		}

		setup();

		MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Outline
		RenderSystem.disableCull();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.lineWidth(lineWidth);

		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		Vertexer.vertexBoxLines(matrices, buffer, Boxes.moveToZero(box), color, excludeDirs);
		tessellator.draw();

		RenderSystem.enableCull();

		cleanup();
	}

	// -------------------- Lines --------------------

	public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, LineColor color, float width) {
		if (!getFrustum().isVisible(new Box(new BlockPos(x1, y1, z1))) && !getFrustum().isVisible(new Box(new BlockPos(x2, y2, z2)))) {
			return;
		}

		setup();

		MatrixStack matrices = matrixFrom(x1, y1, z1);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Line
		RenderSystem.disableDepthTest();
		RenderSystem.disableCull();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.lineWidth(width);

		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		Vertexer.vertexLine(matrices, buffer, 0f, 0f, 0f, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), color);
		tessellator.draw();

		RenderSystem.enableCull();
		RenderSystem.enableDepthTest();
		cleanup();
	}

	public static void drawLine2D(double x1, double y1, double x2, double y2, LineColor color, float width) {
		setup();

		MatrixStack matrices = matrixFrom(x1, y1, 0);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Line
		RenderSystem.disableDepthTest();
		RenderSystem.disableCull();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
		RenderSystem.lineWidth(width);

		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		Vertexer.vertexLine(matrices, buffer, (float) x1, (float) y1, 0f, (float) x2, (float) y2, 0, color);
		tessellator.draw();

		RenderSystem.enableCull();
		RenderSystem.enableDepthTest();
		cleanup();
	}

	// -------------------- World --------------------

	public static void drawWorldText(String text, double x, double y, double z, double scale, int color, boolean background) {
		drawWorldText(text, x, y, z, 0, 0, scale, false, color, background);
	}

	public static void drawWorldText(String text, double x, double y, double z, double offX, double offY, double scale, boolean shadow, int color, boolean background) {
		MatrixStack matrices = matrixFrom(x, y, z);

		Camera camera = Utils.mc.gameRenderer.getCamera();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		matrices.translate(offX, offY, 0);
		matrices.scale(-0.025f * (float) scale, -0.025f * (float) scale, 1);

		int halfWidth = IFont.CONSOLAS.getStringWidth(text) / 2;
		VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());

		if(shadow) {
			matrices.push();
			matrices.translate(1, 1, 0);
			IFont.CONSOLAS.drawString(matrices, text, -halfWidth, 0f, 0x202020, 1);
			immediate.draw();
			matrices.pop();
		}

		if(background) {
			float backgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
			int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

			int xF = -IFont.CONSOLAS.getStringWidth(text) / 2;
			DrawableHelper.fill(matrices, xF - 1, -2, IFont.CONSOLAS.getStringWidth(text) / 2 + 3, IFont.CONSOLAS.getFontHeight() + 1, backgroundColor);
		}
		IFont.CONSOLAS.drawString(matrices, text, -halfWidth, 0f, color, 1);
		immediate.draw();

		RenderSystem.disableBlend();
	}

	public static void drawWorldGuiItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
		if (item.isEmpty()) {
			return;
		}

		MatrixStack matrices = matrixFrom(x, y, z);

		Camera camera = Utils.mc.gameRenderer.getCamera();
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

		matrices.translate(offX, offY, 0);
		matrices.scale((float) scale, (float) scale, 0.001f);

		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		Vector3f[] currentLight = getCurrentLight();
		DiffuseLighting.disableGuiDepthLighting();

		Utils.mc.getBufferBuilders().getEntityVertexConsumers().draw();

		Utils.mc.getItemRenderer().renderItem(item, ModelTransformation.Mode.GUI, 0xF000F0,
				OverlayTexture.DEFAULT_UV, matrices, Utils.mc.getBufferBuilders().getEntityVertexConsumers(), 0);

		Utils.mc.getBufferBuilders().getEntityVertexConsumers().draw();

		RenderSystem.setShaderLights(currentLight[0], currentLight[1]);
		RenderSystem.disableBlend();
	}

	// -------------------- Other --------------------
	public static void drawFullCircle(MatrixStack m, float x, float y, float size, int color) {
		m.push();
		m.scale(size, size, 1);
		if(color != CustomColor.fromRGBA(255, 255, 255, 0)) {
			IFont.CONSOLAS.drawString(m, ".", x, y, color, 1);
		}
		m.pop();
	}

	// -------------------- Utils --------------------

	public static void glColor(int hex) {
		float alpha = (hex >> 24 & 0xFF) / 255.0F;
		float red = (hex >> 16 & 0xFF) / 255.0F;
		float green = (hex >> 8 & 0xFF) / 255.0F;
		float blue = (hex & 0xFF) / 255.0F;
		GL11.glColor4f(red, green, blue, alpha);
	}

	public static MatrixStack matrixFrom(double x, double y, double z) {
		MatrixStack matrices = new MatrixStack();

		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));

		matrices.translate(x - camera.getPos().x, y - camera.getPos().y, z - camera.getPos().z);

		return matrices;
	}

	public static Vec3d getInterpolationOffset(Entity e) {
		if (MinecraftClient.getInstance().isPaused()) {
			return Vec3d.ZERO;
		}

		double tickDelta = (double) MinecraftClient.getInstance().getTickDelta();
		return new Vec3d(
				e.getX() - MathHelper.lerp(tickDelta, e.lastRenderX, e.getX()),
				e.getY() - MathHelper.lerp(tickDelta, e.lastRenderY, e.getY()),
				e.getZ() - MathHelper.lerp(tickDelta, e.lastRenderZ, e.getZ()));
	}

	public static void setup() {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
	}

	public static void cleanup() {
		RenderSystem.disableBlend();
		RenderSystem.enableTexture();
	}

	public static Vector3f[] getCurrentLight() {
		if (shaderLightField == null) {
			shaderLightField = FieldUtils.getField(RenderSystem.class, "shaderLightDirections", true);
		}

		try {
			return (Vector3f[]) shaderLightField.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
