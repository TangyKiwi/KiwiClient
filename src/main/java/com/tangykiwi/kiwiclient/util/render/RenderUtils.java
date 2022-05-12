package com.tangykiwi.kiwiclient.util.render;

import com.mojang.blaze3d.systems.RenderSystem;
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
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;

public class RenderUtils {
	private static Field shaderLightField;

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

	// -------------------- Fill Boxes --------------------

	public static void drawBoxFill(BlockPos blockPos, QuadColor color, Direction... excludeDirs) {
		drawBoxFill(new Box(blockPos), color, excludeDirs);
	}

	public static void drawBoxFill(Box box, QuadColor color, Direction... excludeDirs) {
		if (!FrustumUtils.isBoxVisible(box)) {
			return;
		}

		setup();

		MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Fill
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

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
		if (!FrustumUtils.isBoxVisible(box)) {
			return;
		}

		setup();

		MatrixStack matrices = matrixFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Outline
		RenderSystem.disableCull();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
		RenderSystem.lineWidth(lineWidth);

		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		Vertexer.vertexBoxLines(matrices, buffer, Boxes.moveToZero(box), color, excludeDirs);
		tessellator.draw();

		RenderSystem.enableCull();

		cleanup();
	}

	// -------------------- Quads --------------------

	public static void drawQuadFill(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, int cullMode, QuadColor color) {
		if (!FrustumUtils.isPointVisible(x1, y1, z1) && !FrustumUtils.isPointVisible(x2, y2, z2)
				&& !FrustumUtils.isPointVisible(x3, y3, z3) && !FrustumUtils.isPointVisible(x4, y4, z4)) {
			return;
		}

		setup();

		MatrixStack matrices = matrixFrom(x1, y1, z1);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Fill
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		Vertexer.vertexQuad(matrices, buffer,
				0f, 0f, 0f,
				(float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1),
				(float) (x3 - x1), (float) (y3 - y1), (float) (z3 - z1),
				(float) (x4 - x1), (float) (y4 - y1), (float) (z4 - z1),
				cullMode, color);
		tessellator.draw();

		cleanup();
	}

	public static void drawQuadOutline(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, float lineWidth, QuadColor color) {
		if (!FrustumUtils.isPointVisible(x1, y1, z1) && !FrustumUtils.isPointVisible(x2, y2, z2)
				&& !FrustumUtils.isPointVisible(x3, y3, z3) && !FrustumUtils.isPointVisible(x4, y4, z4)) {
			return;
		}

		setup();

		MatrixStack matrices = matrixFrom(x1, y1, z1);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		int[] colors = color.getAllColors();

		// Outline
		RenderSystem.disableCull();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
		RenderSystem.lineWidth(lineWidth);

		buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		Vertexer.vertexLine(matrices, buffer, 0f, 0f, 0f, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), LineColor.gradient(colors[0], colors[1]));
		Vertexer.vertexLine(matrices, buffer, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), (float) (x3 - x1), (float) (y3 - y1), (float) (z3 - z1), LineColor.gradient(colors[1], colors[2]));
		Vertexer.vertexLine(matrices, buffer, (float) (x3 - x1), (float) (y3 - y1), (float) (z3 - z1), (float) (x4 - x1), (float) (y4 - y1), (float) (z4 - z1), LineColor.gradient(colors[2], colors[3]));
		Vertexer.vertexLine(matrices, buffer, (float) (x4 - x1), (float) (y4 - y1), (float) (z4 - z1), 0f, 0f, 0f, LineColor.gradient(colors[3], colors[0]));
		tessellator.draw();

		RenderSystem.enableCull();
		cleanup();
	}

	// -------------------- Lines --------------------

	public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, LineColor color, float width) {
		if (!FrustumUtils.isPointVisible(x1, y1, z1) && !FrustumUtils.isPointVisible(x2, y2, z2)) {
			return;
		}

		setup();

		MatrixStack matrices = matrixFrom(x1, y1, z1);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		// Line
		RenderSystem.disableDepthTest();
		RenderSystem.disableCull();
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
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
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
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
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));

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
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-camera.getYaw()));
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));

		matrices.translate(offX, offY, 0);
		matrices.scale((float) scale, (float) scale, 0.001f);

		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f));

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		Vec3f[] currentLight = getCurrentLight();
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
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));

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

	public static Vec3f[] getCurrentLight() {
		if (shaderLightField == null) {
			shaderLightField = FieldUtils.getField(RenderSystem.class, "shaderLightDirections", true);
		}

		try {
			return (Vec3f[]) shaderLightField.get(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
