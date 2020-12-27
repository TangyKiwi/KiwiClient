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

}
