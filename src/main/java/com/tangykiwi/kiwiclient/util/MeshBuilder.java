package com.tangykiwi.kiwiclient.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.event.RenderWorldEvent;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;

import static org.lwjgl.opengl.GL11.*;

public class MeshBuilder {
    private final BufferBuilder buffer;
    private double offsetX, offsetY, offsetZ;

    public boolean depthTest = false;
    public boolean texture = false;

    public MeshBuilder(int initialCapacity) {
        buffer = new BufferBuilder(initialCapacity);
    }

    public MeshBuilder() {
        buffer = new BufferBuilder(2097152);
    }

    public void begin(RenderWorldEvent event, DrawMode drawMode, VertexFormat format) {
        if (event != null) {
            offsetX = -event.offsetX;
            offsetY = -event.offsetY;
            offsetZ = -event.offsetZ;
        } else {
            offsetX = 0;
            offsetY = 0;
            offsetZ = 0;
        }

        buffer.begin(drawMode.toOpenGl(), format);
    }

    public void end() {
        glPushMatrix();
        RenderSystem.multMatrix(CustomMatrix.getTop());

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        if (depthTest) RenderSystem.enableDepthTest();
        else RenderSystem.disableDepthTest();
        RenderSystem.disableAlphaTest();
        if (texture) RenderSystem.enableTexture();
        else RenderSystem.disableTexture();
        RenderSystem.disableLighting();
        RenderSystem.disableCull();
        glEnable(GL_LINE_SMOOTH);
        RenderSystem.lineWidth(1);
        RenderSystem.color4f(1, 1, 1, 1);
        GlStateManager.shadeModel(GL_SMOOTH);

        buffer.end();
        BufferRenderer.draw(buffer);

        RenderSystem.enableAlphaTest();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        glDisable(GL_LINE_SMOOTH);

        glPopMatrix();
    }

    public boolean isBuilding() {
        return buffer.isBuilding();
    }

    public MeshBuilder pos(double x, double y, double z) {
        buffer.vertex(x + offsetX, y + offsetY, z + offsetZ);
        return this;
    }

    public MeshBuilder texture(double x, double y) {
        buffer.texture((float) (x + offsetX), (float) (y + offsetY));
        return this;
    }

    public MeshBuilder color(CustomColor color) {
        buffer.color(color.r, color.g, color.b, color.a);
        return this;
    }

    public MeshBuilder color(int color) {
        buffer.color(CustomColor.toRGBAR(color), CustomColor.toRGBAG(color), CustomColor.toRGBAB(color), CustomColor.toRGBAA(color));
        return this;
    }

    public void endVertex() {
        buffer.next();
    }

    // NORMAL

    public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, CustomColor color) {
        pos(x1, y1, z1).color(color).endVertex();
        pos(x2, y2, z2).color(color).endVertex();
        pos(x3, y3, z3).color(color).endVertex();

        pos(x1, y1, z1).color(color).endVertex();
        pos(x3, y3, z3).color(color).endVertex();
        pos(x4, y4, z4).color(color).endVertex();
    }

    public void quad(double x, double y, double width, double height, CustomColor color) {
        quad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, color);
    }

    public void gradientQuad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, CustomColor startColor, CustomColor endColor) {
        pos(x1, y1, z1).color(startColor).endVertex();
        pos(x2, y2, z2).color(endColor).endVertex();
        pos(x3, y3, z3).color(endColor).endVertex();

        pos(x1, y1, z1).color(startColor).endVertex();
        pos(x3, y3, z3).color(endColor).endVertex();
        pos(x4, y4, z4).color(startColor).endVertex();
    }

    public void gradientQuad(double x, double y, double width, double height, CustomColor startColor, CustomColor endColor) {
        gradientQuad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0,startColor, endColor);
    }

    public void texQuad(double x, double y, double width, double height, double srcX, double srcY, double srcWidth, double srcHeight, CustomColor color1, CustomColor color2, CustomColor color3, CustomColor color4) {
        pos(x, y, 0).texture(srcX, srcY).color(color1).endVertex();
        pos(x + width, y, 0).texture(srcX + srcWidth, srcY).color(color2).endVertex();
        pos(x + width, y + height, 0).texture(srcX + srcWidth, srcY + srcHeight).color(color3).endVertex();

        pos(x, y, 0).texture(srcX, srcY).color(color1).endVertex();
        pos(x + width, y + height, 0).texture(srcX + srcWidth, srcY + srcHeight).color(color3).endVertex();
        pos(x, y + height, 0).texture(srcX, srcY + srcHeight).color(color4).endVertex();
    }

    public void boxSides(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor color, int excludeDir) {
        if (CustomDirection.is(excludeDir, CustomDirection.DOWN)) quad(x1, y1, z1, x1, y1, z2, x2, y1, z2, x2, y1, z1, color); // Bottom
        if (CustomDirection.is(excludeDir, CustomDirection.UP)) quad(x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1, color); // Top

        if (CustomDirection.is(excludeDir, CustomDirection.NORTH)) quad(x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, color); // Front
        if (CustomDirection.is(excludeDir, CustomDirection.SOUTH)) quad(x1, y1, z2, x1, y2, z2, x2, y2, z2, x2, y1, z2, color); // Back

        if (CustomDirection.is(excludeDir, CustomDirection.WEST)) quad(x1, y1, z1, x1, y2, z1, x1, y2, z2, x1, y1, z2, color); // Left
        if (CustomDirection.is(excludeDir, CustomDirection.EAST)) quad(x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, color); // Right
    }

    public void gradientBoxSides(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor startColor, CustomColor endColor) {
        gradientQuad(x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, startColor, endColor); // Front
        gradientQuad(x1, y1, z2, x1, y2, z2, x2, y2, z2, x2, y1, z2, startColor, endColor); // Back
        gradientQuad(x1, y1, z1, x1, y2, z1, x1, y2, z2, x1, y1, z2, startColor, endColor); // Left
        gradientQuad(x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, startColor, endColor); // Right
    }

    // LINES

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor color) {
        pos(x1, y1, z1).color(color).endVertex();
        pos(x2, y2, z2).color(color).endVertex();
    }

    public void gradientLine(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor startColor, CustomColor endColor) {
        pos(x1, y1, z1).color(startColor).endVertex();
        pos(x2, y2, z2).color(endColor).endVertex();
    }

    public void boxEdges(double x1, double y1, double z1, double x2, double y2, double z2, CustomColor color, int excludeDir) {
        if (CustomDirection.is(excludeDir, CustomDirection.WEST) && CustomDirection.is(excludeDir, CustomDirection.NORTH)) line(x1, y1, z1, x1, y2, z1, color);
        if (CustomDirection.is(excludeDir, CustomDirection.WEST) && CustomDirection.is(excludeDir, CustomDirection.SOUTH)) line(x1, y1, z2, x1, y2, z2, color);
        if (CustomDirection.is(excludeDir, CustomDirection.EAST) && CustomDirection.is(excludeDir, CustomDirection.NORTH)) line(x2, y1, z1, x2, y2, z1, color);
        if (CustomDirection.is(excludeDir, CustomDirection.EAST) && CustomDirection.is(excludeDir, CustomDirection.SOUTH)) line(x2, y1, z2, x2, y2, z2, color);

        if (CustomDirection.is(excludeDir, CustomDirection.NORTH)) line(x1, y1, z1, x2, y1, z1, color);
        if (CustomDirection.is(excludeDir, CustomDirection.NORTH)) line(x1, y2, z1, x2, y2, z1, color);
        if (CustomDirection.is(excludeDir, CustomDirection.SOUTH)) line(x1, y1, z2, x2, y1, z2, color);
        if (CustomDirection.is(excludeDir, CustomDirection.SOUTH)) line(x1, y2, z2, x2, y2, z2, color);

        if (CustomDirection.is(excludeDir, CustomDirection.WEST)) line(x1, y1, z1, x1, y1, z2, color);
        if (CustomDirection.is(excludeDir, CustomDirection.WEST)) line(x1, y2, z1, x1, y2, z2, color);
        if (CustomDirection.is(excludeDir, CustomDirection.EAST)) line(x2, y1, z1, x2, y1, z2, color);
        if (CustomDirection.is(excludeDir, CustomDirection.EAST)) line(x2, y2, z1, x2, y2, z2, color);
    }

    public void boxEdges(double x, double y, double width, double height, CustomColor color) {
        boxEdges(x,y, 0, x + width, y + height, 0, color, 0);
    }
}