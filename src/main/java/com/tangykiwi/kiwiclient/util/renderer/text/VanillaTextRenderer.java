/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package com.tangykiwi.kiwiclient.util.renderer.text;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.util.CustomColor;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class VanillaTextRenderer implements TextRenderer {
    public static final TextRenderer INSTANCE = new VanillaTextRenderer();

    private final BufferBuilder buffer = new BufferBuilder(2048);
    private final VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(buffer);
    private final Matrix4f emptyMatrix = new Matrix4f();

    // Vanilla font is almost twice as small as our custom font (vanilla = 9, custom = 18)
    private double scale = 1.74;
    private boolean building;
    private double alpha = 1;

    private VanillaTextRenderer() {
        // Use INSTANCE

        emptyMatrix.loadIdentity();
    }

    @Override
    public void setAlpha(double a) {
        alpha = a;
    }

    @Override
    public double getWidth(String text, int length, boolean shadow) {
        String string = text;
        if (length != string.length()) string = string.substring(0, length);
        return (Utils.mc.textRenderer.getWidth(string) + (shadow ? 1 : 0)) * scale;
    }

    @Override
    public double getHeight(boolean shadow) {
        return (Utils.mc.textRenderer.fontHeight + (shadow ? 1 : 0)) * scale;
    }

    @Override
    public void begin(double scale, boolean scaleOnly, boolean big) {
        if (building) throw new RuntimeException("VanillaTextRenderer.begin() called twice");

        // Vanilla font is twice as small as our custom font (vanilla = 9, custom = 18)
        this.scale = scale * 1.74;
        this.building = true;
    }

    @Override
    public double draw(MatrixStack m, String text, double x, double y, CustomColor color, boolean shadow) {
        boolean wasBuilding = building;
        if (!wasBuilding) begin();

        x += 0.5 * scale;
        y += 0.5 * scale;

        int preA = color.a;
        color.a = (int) ((color.a / 255 * alpha) * 255);

        double width = Utils.mc.textRenderer.draw(text, (float) (x / scale), (float) (y / scale), color.getPacked(), shadow, emptyMatrix, immediate, true, 0, 15728880);

        color.a = preA;

        if (!wasBuilding) end();
        return width * scale;
    }

    @Override
    public boolean isBuilding() {
        return building;
    }

    @Override
    public void end(MatrixStack matrices) {
        if (!building) throw new RuntimeException("VanillaTextRenderer.end() called without calling begin()");

        MatrixStack matrixStack = RenderSystem.getModelViewStack();

        RenderSystem.disableDepthTest();
        matrixStack.push();
        if (matrices != null) matrixStack.method_34425(matrices.peek().getModel());
        matrixStack.scale((float) scale, (float) scale, 1);
        RenderSystem.applyModelViewMatrix();

        immediate.draw();

        matrixStack.pop();
        RenderSystem.enableDepthTest();
        RenderSystem.applyModelViewMatrix();

        // Vanilla font is twice as small as our custom font (vanilla = 9, custom = 18)
        this.scale = 1.74;
        this.building = false;
    }
}