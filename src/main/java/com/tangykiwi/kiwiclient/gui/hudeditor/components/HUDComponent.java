package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

public abstract class HUDComponent {
    private String name;
    private float x, y;
    private int width, height;

    public HUDComponent(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public void render(DrawContext context, FontRenderer fontRenderer) {
        
    }

    public String getName() {
        return name;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getColorString(int value, int best, int good, int mid, int bad, int worst, Boolean reverse) {
        Color color = Color.GRAY; // default
        if (!reverse ? value > best : value < best) {color = Color.GREEN;}
        else if (!reverse ? value > good : value < good) {color = Color.YELLOW;}
        else if (!reverse ? value > mid : value < mid) {color = new Color(255, 191, 0);}
        else if (!reverse ? value > bad : value < bad) {color = new Color(255, 191, 0);}
        else if (!reverse ? value > worst : value < worst) {color = Color.ORANGE;}
        else {color = Color.RED;}
        return (int) Long.parseLong(Integer.toHexString(color.getRGB()), 16);
    }
}