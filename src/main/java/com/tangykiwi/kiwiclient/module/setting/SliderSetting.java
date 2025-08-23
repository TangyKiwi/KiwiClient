package com.tangykiwi.kiwiclient.module.setting;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;

import net.minecraft.client.gui.DrawContext;

public class SliderSetting extends Setting<Double> {
    public double min;
    public double max;
    // value = double value
    public int decimals;

    public SliderSetting(String name, String desc, double min, double max, double value, int decimals) {
        super(name, desc);
        this.min = min;
        this.max = max;
        this.setValue(value);
        this.decimals = decimals;
    }

    public double getValueD() {
        return round(this.getValue(), decimals);
    }

    public float getValueFloat() {
        return getValue().floatValue();
    }

    public int getValueInt() {
        return getValue().intValue();
    }

    public long getValueLong() {
        return getValue().longValue();
    }

    public double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public void render(DrawContext context, CategoryWindow window, int curYoffset) {
        int fontHeight = (int) window.fontHeight;
        height = fontHeight + 1;
    }

    @Override
    public void read(JsonElement je) {
        setValue(je.getAsDouble());
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(getValue());
    }
}
