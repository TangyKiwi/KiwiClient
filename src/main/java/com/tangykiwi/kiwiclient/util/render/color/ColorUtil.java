package com.tangykiwi.kiwiclient.util.render.color;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.Random;

public class ColorUtil {

    public static int getRainbow(float seconds, float saturation, float brightness) {
        float hue = (System.currentTimeMillis() % (int) (seconds * 1000)) / (float) (seconds * 1000);
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return color;
    }

    public static int getRainbow(float seconds, float saturation, float brightness, long index) {
        float hue = ((System.currentTimeMillis() + index) % (int) (seconds * 1000)) / (float) (seconds * 1000);
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return color;
    }

    public static int randomColor() {
        Random rng = new Random();
        float r = rng.nextFloat();
        float g = rng.nextFloat();
        float b = rng.nextFloat();
        Color color = new Color(r, g, b);
        return color.getRGB();
    }

    public static void fillGradient(MatrixStack matrixStack, int xStart, int yStart, int xEnd, int yEnd, int colorStart, int colorEnd) {
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        //RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        //RenderSystem.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float f = (float)(colorStart >> 24 & 255) / 255.0F;
        float g = (float)(colorStart >> 16 & 255) / 255.0F;
        float h = (float)(colorStart >> 8 & 255) / 255.0F;
        float i = (float)(colorStart & 255) / 255.0F;
        float j = (float)(colorEnd >> 24 & 255) / 255.0F;
        float k = (float)(colorEnd >> 16 & 255) / 255.0F;
        float l = (float)(colorEnd >> 8 & 255) / 255.0F;
        float m = (float)(colorEnd & 255) / 255.0F;
        bufferBuilder.vertex(matrixStack.peek().getPositionMatrix(), (float)xEnd, (float)yStart, (float)0).color(g, h, i, f).next();
        bufferBuilder.vertex(matrixStack.peek().getPositionMatrix(), (float)xStart, (float)yStart, (float)0).color(g, h, i, f).next();
        bufferBuilder.vertex(matrixStack.peek().getPositionMatrix(), (float)xStart, (float)yEnd, (float)0).color(k, l, m, j).next();
        bufferBuilder.vertex(matrixStack.peek().getPositionMatrix(), (float)xEnd, (float)yEnd, (float)0).color(k, l, m, j).next();
        tessellator.draw();
        //RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        //RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    /**
     * Returns a color based on the range provided. 
     */
    public static int getColorString(int value, int best, int good, int mid, int bad, int worst, Boolean reverse) {
        Color color = Color.GRAY; // default
        if (!reverse ? value > best : value < best) {color = Color.GREEN;}
        else if (!reverse ? value > good : value < good) {color = Color.YELLOW;}
        else if (!reverse ? value > mid : value < mid) {color = new Color(255, 191, 0);}
        else if (!reverse ? value > bad : value < bad) {color = new Color(255, 191, 0);}
        else if (!reverse ? value > worst : value < worst) {color = Color.ORANGE;}
        else {color = Color.RED;}
        return (int) Long.parseLong(Integer.toHexString(color.getRGB()), 16);
    }

    public static int guiColour() {
        return (0xff << 24) | ((85 & 0xff) << 16) | ((85 & 0xff) << 8) | (255 & 0xff);
    }

}
