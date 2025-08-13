package com.tangykiwi.kiwiclient.gui.particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

import org.joml.Matrix3x2fStack;

import com.tangykiwi.kiwiclient.util.render.RenderUtils;

public class Particle {
    private float posX;
    private float posY;
    private float alpha;
    private float size;
    private float speed;
    private float dx;
    private float dy;
    public MinecraftClient mc = MinecraftClient.getInstance();

    public Particle(float posX, float posY, float size, float speed, float alpha) {
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.speed = speed;
        this.alpha = alpha;
    }

    public void render(DrawContext m) {
        alpha -= 0.3F;
        if (alpha <= 0) {
            alpha = 0;
            return;
        }

        posX = posX + dx * speed;
        posY = posY + dy * speed;
        RenderUtils.drawCircle(m, posX, posY, size, new Color(255, 255, 255, (int) alpha).getRGB());
    }

    public float getPosX() {
        return this.posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }
}
