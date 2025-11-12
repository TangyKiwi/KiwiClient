package com.tangykiwi.kiwiclient.util.render;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class Vertexer {
    public static void vertexLine(MatrixStack matrices, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, int color) {
        Matrix4f model = matrices.peek().getPositionMatrix();
        Matrix3f normal = matrices.peek().getNormalMatrix();

        Vector3f normalVec = getNormal(normal, x1, y1, z1, x2, y2, z2);
        vertexConsumer.vertex(model, x1, y1, z1).color(color).normal(matrices.peek(), normalVec.x(), normalVec.y(), normalVec.z());
        vertexConsumer.vertex(model, x2, y2, z2).color(color).normal(matrices.peek(), normalVec.x(), normalVec.y(), normalVec.z());
    }

    public static Vector3f getNormal(Matrix3f normal, float x1, float y1, float z1, float x2, float y2, float z2) {
		float xNormal = x2 - x1;
		float yNormal = y2 - y1;
		float zNormal = z2 - z1;
		float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

		return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
	}

    public static void vertexBoxOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, int color, Direction... excludeDirs) {
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;    
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        boolean exDown = ArrayUtils.contains(excludeDirs, Direction.DOWN);
		boolean exWest = ArrayUtils.contains(excludeDirs, Direction.WEST);
		boolean exEast = ArrayUtils.contains(excludeDirs, Direction.EAST);
		boolean exNorth = ArrayUtils.contains(excludeDirs, Direction.NORTH);
		boolean exSouth = ArrayUtils.contains(excludeDirs, Direction.SOUTH);
		boolean exUp = ArrayUtils.contains(excludeDirs, Direction.UP);

        if (!exDown) {
            vertexLine(matrices, vertexConsumer, x1, y1, z1, x2, y1, z1, color);
            vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y1, z2, color);
            vertexLine(matrices, vertexConsumer, x2, y1, z2, x1, y1, z2, color);
            vertexLine(matrices, vertexConsumer, x1, y1, z2, x1, y1, z1, color);
        }
		if (!exWest) {
			if (exDown) vertexLine(matrices, vertexConsumer, x1, y1, z1, x1, y1, z2, color);
			vertexLine(matrices, vertexConsumer, x1, y1, z2, x1, y2, z2, color);
			vertexLine(matrices, vertexConsumer, x1, y1, z1, x1, y2, z1, color);
			if (exUp) vertexLine(matrices, vertexConsumer, x1, y2, z1, x1, y2, z2, color);
		}

		if (!exEast) {
			if (exDown) vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y1, z2, color);
			vertexLine(matrices, vertexConsumer, x2, y1, z2, x2, y2, z2, color);
			vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y2, z1, color);
			if (exUp) vertexLine(matrices, vertexConsumer, x2, y2, z1, x2, y2, z2, color);
		}

		if (!exNorth) {
			if (exDown) vertexLine(matrices, vertexConsumer, x1, y1, z1, x2, y1, z1, color);
			if (exEast) vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y2, z1, color);
			if (exWest) vertexLine(matrices, vertexConsumer, x1, y1, z1, x1, y2, z1, color);
			if (exUp) vertexLine(matrices, vertexConsumer, x1, y2, z1, x2, y2, z1, color);
		}

		if (!exSouth) {
			if (exDown) vertexLine(matrices, vertexConsumer, x1, y1, z2, x2, y1, z2, color);
			if (exEast) vertexLine(matrices, vertexConsumer, x2, y1, z2, x2, y2, z2, color);
			if (exWest) vertexLine(matrices, vertexConsumer, x1, y1, z2, x1, y2, z2, color);
			if (exUp) vertexLine(matrices, vertexConsumer, x1, y2, z2, x2, y2, z2, color);
		}

		if (!exUp) {
			vertexLine(matrices, vertexConsumer, x1, y2, z1, x2, y2, z1, color);
			vertexLine(matrices, vertexConsumer, x2, y2, z1, x2, y2, z2, color);
			vertexLine(matrices, vertexConsumer, x2, y2, z2, x1, y2, z2, color);
			vertexLine(matrices, vertexConsumer, x1, y2, z2, x1, y2, z1, color);
		}
    }

    public static final int CULL_BACK = 0;
	public static final int CULL_FRONT = 1;
	public static final int CULL_NONE = 2;

    public static void vertexBoxFilled(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, int color, Direction... excludeDirs) {
        float x1 = (float) box.minX;
		float y1 = (float) box.minY;
		float z1 = (float) box.minZ;
		float x2 = (float) box.maxX;
		float y2 = (float) box.maxY;
		float z2 = (float) box.maxZ;

		int cullMode = excludeDirs.length == 0 ? CULL_BACK : CULL_NONE;

        if (!ArrayUtils.contains(excludeDirs, Direction.DOWN)) {
			vertexQuad(matrices, vertexConsumer, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, cullMode, color);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.WEST)) {
			vertexQuad(matrices, vertexConsumer, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1, cullMode, color);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.EAST)) {
			vertexQuad(matrices, vertexConsumer, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, cullMode, color);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.NORTH)) {
			vertexQuad(matrices, vertexConsumer, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, cullMode, color);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.SOUTH)) {
			vertexQuad(matrices, vertexConsumer, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2, cullMode, color);
		}

		if (!ArrayUtils.contains(excludeDirs, Direction.UP)) {
			vertexQuad(matrices, vertexConsumer, x1, y2, z2, x2, y2, z2, x2, y2, z1, x1, y2, z1, cullMode, color);
		}
    }

    public static void vertexQuad(MatrixStack matrices, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int cullMode, int color) {
		if (cullMode != CULL_FRONT) {
			vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x1, y1, z1).color(color);
			vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x2, y2, z2).color(color);
			vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x3, y3, z3).color(color);
			vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x4, y4, z4).color(color);
		}

		if (cullMode != CULL_BACK) {
			vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x4, y4, z4).color(color);
			vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x3, y3, z3).color(color);
			vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x2, y2, z2).color(color);
			vertexConsumer.vertex(matrices.peek().getPositionMatrix(), x1, y1, z1).color(color);
		}
	}
}
