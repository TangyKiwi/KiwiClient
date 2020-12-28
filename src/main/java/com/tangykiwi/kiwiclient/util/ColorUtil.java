package com.tangykiwi.kiwiclient.util;

import java.awt.Color;
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

    /**
     * Returns a color based on the range provided. 
     */
    public static int getColorString(int value, int best, int good, int mid, int bad, int worst) {
        Color color = Color.GRAY;
        if (value >= best) {color = Color.GREEN;}
        else if (value >= good && value < best) {color = Color.YELLOW;}
        else if (value >= bad && value < good) {color = new Color(255, 191, 0);}
        else if (value >= worst && value < bad) {color = Color.ORANGE;}
        else if (value < worst) {color = Color.RED;}
        return (int) Long.parseLong(Integer.toHexString(color.getRGB()), 16);
    }

}
