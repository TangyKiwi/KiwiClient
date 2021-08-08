package com.tangykiwi.kiwiclient.modules.settings;

import com.tangykiwi.kiwiclient.gui.ModuleWindow;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderSetting extends Settings {

    public double min;
    public double max;
    private double value;
    public int decimals;
    public String text;

    protected double defaultValue;

    public SliderSetting(String text, double min, double max, double value, int decimals) {
        this.min = min;
        this.max = max;
        this.value = value;
        this.decimals = decimals;
        this.text = text;

        defaultValue = value;
    }

    public double getValue() {
        return round(value, decimals);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String getName() {
        return text;
    }

    public void render(ModuleWindow window, MatrixStack matrix, int x, int y, int len) {
        int pixels = (int) Math.round(MathHelper.clamp((len - 2) * ((getValue() - min) / (max - min)), 0, len - 2));
        window.fillGradient(matrix, x + 1, y, x + pixels, y + 12, ColorUtil.guiColour(), ColorUtil.guiColour());

        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrix,
                text + ": " + (decimals == 0 && getValue() > 100 ? Integer.toString((int) getValue()) : getValue()),
                x + 2, y + 2, window.mouseOver(x, y, x + len, y + 12) ? 0xcfc3cf : 0xcfe0cf);

        if (window.mouseOver(x + 1, y, x + len - 2, y + 12)) {
            if (window.lmHeld) {
                int percent = ((window.mouseX - x) * 100) / (len - 2);

                setValue(round(percent * ((max - min) / 100) + min, decimals));
            }

            if (window.mwScroll != 0) {
                double units = 1 / (Math.pow(10, decimals));

                setValue(MathHelper.clamp(getValue() + units * window.mwScroll, min, max));
            }
        }
    }

    public SliderSetting withDesc(String desc) {
        description = desc;
        return this;
    }

    public int getHeight(int len) {
        return 12;
    }
}
