package com.tangykiwi.kiwiclient.util.render;

import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class CustomMatrix {
    private static MatrixStack matrixStack;

    public static void begin(MatrixStack matrixStack) {
        CustomMatrix.matrixStack = matrixStack;
    }

    public static void push() {
        matrixStack.push();
    }

    public static void translate(double x, double y, double z) {
        matrixStack.translate(x, y, z);
    }

    public static void rotate(double angle, double x, double y, double z) {
        matrixStack.multiply(Utils.quaternionf((float) (x * angle), (float) (y * angle), (float) (z * angle), true));
    }

    public static void scale(double x, double y, double z) {
        matrixStack.scale((float) x, (float) y, (float) z);
    }

    public static void pop() {
        matrixStack.pop();
    }

    public static Matrix4f getTop() {
        return matrixStack.peek().getPositionMatrix();
    }

    public static MatrixStack getMatrixStack() {
        return matrixStack;
    }
}
