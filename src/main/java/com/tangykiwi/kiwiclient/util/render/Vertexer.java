package com.tangykiwi.kiwiclient.util.render;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

public class Vertexer {
    public static void vertexLine(PoseStack matrices, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, int color, float lineWidth) {
        Matrix4f model = matrices.last().pose();
        Matrix3f normal = matrices.last().normal();

        Vector3f normalVec = getNormal(normal, x1, y1, z1, x2, y2, z2);
        vertexConsumer.addVertex(model, x1, y1, z1).setColor(color).setNormal(matrices.last(), normalVec.x(), normalVec.y(), normalVec.z())/*.setLineWidth(lineWidth)*/;
        vertexConsumer.addVertex(model, x2, y2, z2).setColor(color).setNormal(matrices.last(), normalVec.x(), normalVec.y(), normalVec.z())/*.setLineWidth(lineWidth)*/;
    }

    public static Vector3f getNormal(Matrix3f normal, float x1, float y1, float z1, float x2, float y2, float z2) {
		float xNormal = x2 - x1;
		float yNormal = y2 - y1;
		float zNormal = z2 - z1;
		float normalSqrt = Mth.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

		return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
	}

    public static void vertexBoxOutline(PoseStack matrices, VertexConsumer vertexConsumer, AABB box, int color, float lineWidth, Direction... excludeDirs) {
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
            vertexLine(matrices, vertexConsumer, x1, y1, z1, x2, y1, z1, color, lineWidth);
            vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y1, z2, color, lineWidth);
            vertexLine(matrices, vertexConsumer, x2, y1, z2, x1, y1, z2, color, lineWidth);
            vertexLine(matrices, vertexConsumer, x1, y1, z2, x1, y1, z1, color, lineWidth);
        }
		if (!exWest) {
			if (exDown) vertexLine(matrices, vertexConsumer, x1, y1, z1, x1, y1, z2, color, lineWidth);
			vertexLine(matrices, vertexConsumer, x1, y1, z2, x1, y2, z2, color, lineWidth);
			vertexLine(matrices, vertexConsumer, x1, y1, z1, x1, y2, z1, color, lineWidth);
			if (exUp) vertexLine(matrices, vertexConsumer, x1, y2, z1, x1, y2, z2, color, lineWidth);
		}

		if (!exEast) {
			if (exDown) vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y1, z2, color, lineWidth);
			vertexLine(matrices, vertexConsumer, x2, y1, z2, x2, y2, z2, color, lineWidth);
			vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y2, z1, color, lineWidth);
			if (exUp) vertexLine(matrices, vertexConsumer, x2, y2, z1, x2, y2, z2, color, lineWidth);
		}

		if (!exNorth) {
			if (exDown) vertexLine(matrices, vertexConsumer, x1, y1, z1, x2, y1, z1, color, lineWidth);
			if (exEast) vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y2, z1, color, lineWidth);
			if (exWest) vertexLine(matrices, vertexConsumer, x1, y1, z1, x1, y2, z1, color, lineWidth);
			if (exUp) vertexLine(matrices, vertexConsumer, x1, y2, z1, x2, y2, z1, color, lineWidth);
		}

		if (!exSouth) {
			if (exDown) vertexLine(matrices, vertexConsumer, x1, y1, z2, x2, y1, z2, color, lineWidth);
			if (exEast) vertexLine(matrices, vertexConsumer, x2, y1, z2, x2, y2, z2, color, lineWidth);
			if (exWest) vertexLine(matrices, vertexConsumer, x1, y1, z2, x1, y2, z2, color, lineWidth);
			if (exUp) vertexLine(matrices, vertexConsumer, x1, y2, z2, x2, y2, z2, color, lineWidth);
		}

		if (!exUp) {
			vertexLine(matrices, vertexConsumer, x1, y2, z1, x2, y2, z1, color, lineWidth);
			vertexLine(matrices, vertexConsumer, x2, y2, z1, x2, y2, z2, color, lineWidth);
			vertexLine(matrices, vertexConsumer, x2, y2, z2, x1, y2, z2, color, lineWidth);
			vertexLine(matrices, vertexConsumer, x1, y2, z2, x1, y2, z1, color, lineWidth);
		}
    }

    public static final int CULL_BACK = 0;
	public static final int CULL_FRONT = 1;
	public static final int CULL_NONE = 2;

    public static void vertexBoxFilled(PoseStack matrices, VertexConsumer vertexConsumer, AABB box, int color, Direction... excludeDirs) {
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

    public static void vertexQuad(PoseStack matrices, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int cullMode, int color) {
		Matrix4f model = matrices.last().pose();
		if (cullMode != CULL_FRONT) {
			vertexConsumer.addVertex(model, x1, y1, z1).setColor(color);
			vertexConsumer.addVertex(model, x2, y2, z2).setColor(color);
			vertexConsumer.addVertex(model, x3, y3, z3).setColor(color);
			vertexConsumer.addVertex(model, x4, y4, z4).setColor(color);
		}

		if (cullMode != CULL_BACK) {
			vertexConsumer.addVertex(model, x4, y4, z4).setColor(color);
			vertexConsumer.addVertex(model, x3, y3, z3).setColor(color);
			vertexConsumer.addVertex(model, x2, y2, z2).setColor(color);
			vertexConsumer.addVertex(model, x1, y1, z1).setColor(color);
		}
	}
}
