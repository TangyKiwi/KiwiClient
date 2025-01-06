package com.tangykiwi.kiwiclient.module.setting;

import java.math.BigDecimal;
import java.math.RoundingMode;

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

    public double getValue() {
        return round(this.getSValue(), decimals);
    }

    public float getValueFloat() {
        return (float) getValue();
    }

    public int getValueInt() {
        return (int) getValue();
    }

    public long getValueLong() {
        return (long) getValue();
    }

    public double round(double value, int places) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
