package com.tangykiwi.kiwiclient.modules.settings;

import com.tangykiwi.kiwiclient.gui.clickgui.window.ModuleWindow;
import com.tangykiwi.kiwiclient.gui.clickgui.window.Window;
import com.tangykiwi.kiwiclient.util.Utils;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderSetting extends Setting<Double> {

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

        this.setDataValue(value);
        this.setHandler(SettingDataHandler.DOUBLE);
    }

    public double getValue() {
        return round(value, decimals);
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

    public int render(ModuleWindow window, MatrixStack matrices, int x, int y, int len, int index, int m) {
        boolean mo = window.mouseOver(x, y, x + len, y + 12);
        if (mo) {
            DrawableHelper.fill(matrices, x + 1, y, x + len, y + 12, 0x70303070);
        }

        int pixels = (int) Math.round(MathHelper.clamp(len * ((getValue() - min) / (max - min)), 0, len));
        Window.horizontalGradient(matrices, x + 1, y, x + pixels, y + 12,
                mo ? 0xf03078b0 : 0xf03080a0, mo ? 0xf02068c0 : 0xf02070b0);

        IFont.CONSOLAS.drawStringWithShadow(matrices,
                text + ": " + (decimals == 0 ? Integer.toString((int) getValue()) : getValue()),
                x + 3, y + 2, 0xcfe0cf, 1);

        if (window.mouseOver(x + 1, y, x + len, y + 12)) {
            if (window.lmHeld) {
                int percent = ((window.mouseX - x) * 100) / len;

                setValue(round(percent * ((max - min) / 100) + min, decimals));
            }

            if (window.mwScroll != 0 && InputUtil.isKeyPressed(Utils.mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
                double units = 1 / (Math.pow(10, decimals));

                setValue(MathHelper.clamp(getValue() + units * window.mwScroll, min, max));
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F, 0.3F));
            }

            this.setDataValue(value);
        }

        return index;
    }

    public SliderSetting withDesc(String desc) {
        description = desc;
        return this;
    }

    public int getHeight(int len) {
        return 12;
    }

    @Override
    public void setDataValue(Double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(decimals, RoundingMode.HALF_UP);

        this.value = bd.doubleValue();
        super.setDataValue(bd.doubleValue());
    }
}
