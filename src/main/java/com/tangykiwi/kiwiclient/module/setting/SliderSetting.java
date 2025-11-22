package com.tangykiwi.kiwiclient.module.setting;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.lwjgl.glfw.GLFW;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

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
        int x = window.x;
        int y = window.y + curYoffset;
        int width = window.width;
        FontRenderer fontRenderer = window.fontRenderer;
        int fontHeight = (int) window.fontHeight;
        height = fontHeight + 1;

        context.fill(x + 1, y + 1, x + 2, y + fontHeight + 1, 0xff8070b0);

        boolean mo = window.mouseOver(x + 1, y + 1, x + width, y + height + 1);
        if (mo) {
            context.fill(x + 1, y + 1, x + width - 1, y + height, 0x70303070);
        }

        int pixels = (int) Math.round(MathHelper.clamp((width - 1) * ((getValue() - min) / (max - min)), 0, width));
        context.fill(x + 2, y + 1, x + pixels, y + height, mo ? 0xf02068c0 : 0xf02070b0);
        // RenderUtils.fillGradient(x + 1, y, x + pixels, y + fontHeight, mo ? 0xf03078b0 : 0xf03080a0, mo ? 0xf02068c0 : 0xf02070b0);

        fontRenderer.drawString(context, getName() + ": " + (decimals == 0 ? Integer.toString(getValueInt()) : getValue()),
                x + 3, y + 2, 0xcfe0cf);

        if (mo) {
            if (window.lmHeld) {
                int percent = ((window.mouseX - x) * 100) / width;
                setValue(round(percent * (max - min) / 100 + min, decimals));
            }

            if (window.mwScroll != 0 && InputUtil.isKeyPressed(mc.getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
                double units = 1 / (Math.pow(10, decimals));

                setValue(MathHelper.clamp(getValue() + units * window.mwScroll, min, max));
                mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.3F));
            }
        }
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
