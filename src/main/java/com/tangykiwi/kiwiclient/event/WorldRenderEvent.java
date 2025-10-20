package com.tangykiwi.kiwiclient.event;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.joml.Matrix4f;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class WorldRenderEvent extends Event {
    protected MatrixStack matrixStack;
    protected Matrix4f positionMatrix;
    protected Matrix4f projectionMatrix;

    public static class Pre extends WorldRenderEvent {
        public Pre(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
            this.positionMatrix = positionMatrix;
            this.projectionMatrix = projectionMatrix;
            this.matrixStack = new MatrixStack();
            this.matrixStack.multiplyPositionMatrix(positionMatrix);
        }

    }

    public static class Post extends WorldRenderEvent {
        public Post(Matrix4f positionMatrix, Matrix4f projectionMatrix) {
            this.positionMatrix = positionMatrix;
            this.projectionMatrix = projectionMatrix;
            this.matrixStack = new MatrixStack();
            this.matrixStack.multiplyPositionMatrix(positionMatrix);
        }
    }

    public MatrixStack getMatrixStack() { 
        return matrixStack; 
    }

    public Matrix4f getPositionMatrix() { 
        return positionMatrix; 
    }

    public Matrix4f getProjectionMatrix() { 
        return projectionMatrix; 
    }

    public Box getOffsetPos(BlockPos pos) {
        var c = mc.gameRenderer.getCamera().getPos();
        double x = pos.getX() - c.x;
        double y = pos.getY() - c.y;
        double z = pos.getZ() - c.z;
        return new Box(x, y, z, x + 1, y + 1, z + 1);
    }
}
