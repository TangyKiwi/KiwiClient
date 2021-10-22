package com.tangykiwi.kiwiclient.gui.mainmenu.particles;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Random;

public class Particle {
    private float posX;
    private float posY;
    private float alpha;
    private float size;
    private float speed;
    public MinecraftClient mc = MinecraftClient.getInstance();

    public Particle(float posX, float posY, float size, float speed, float alpha) {
        setPosX(posX);
        setPosY(posY);
        setSize(size);
        setSpeed(speed);
        setAlpha(alpha);
    }

    public void render(MatrixStack m, ParticleManager p) {
        setAlpha(getAlpha() - 0.3F);
        if(getAlpha() < 0)
            setAlpha(0);
    }

    public float getPosX() {
        return this.posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return this.posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getSize() {
        return this.size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
