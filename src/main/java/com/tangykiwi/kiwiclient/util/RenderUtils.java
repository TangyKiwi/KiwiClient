package com.tangykiwi.kiwiclient.util;

import com.tangykiwi.kiwiclient.KiwiClient;

import java.awt.*;

public class RenderUtils {
    public static int getGuiScale() {
        return (int) KiwiClient.mc.getWindow().getScaleFactor();
    }

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
}
