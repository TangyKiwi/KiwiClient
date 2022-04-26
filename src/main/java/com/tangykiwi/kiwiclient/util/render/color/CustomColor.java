package com.tangykiwi.kiwiclient.util.render.color;

import com.tangykiwi.kiwiclient.util.ISerializable;
import net.minecraft.nbt.NbtCompound;

public class CustomColor implements ISerializable<CustomColor> {
    public int r, g, b, a;

    public CustomColor() {
        this(255, 255, 255, 255);
    }

    public CustomColor(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        a = 255;

        validate();
    }

    public CustomColor(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        validate();
    }

    public CustomColor(int packed) {
        r = toRGBAR(packed);
        g = toRGBAG(packed);
        b = toRGBAB(packed);
        a = toRGBAA(packed);
    }

    public CustomColor(CustomColor color) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.a = color.a;
    }

    public CustomColor(java.awt.Color color) {
        this.r = color.getRed();
        this.g = color.getGreen();
        this.b = color.getBlue();
        this.a = color.getAlpha();
    }

    public void set(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        validate();
    }

    public void set(CustomColor value) {
        r = value.r;
        g = value.g;
        b = value.b;
        a = value.a;

        validate();
    }

    public void validate() {
        if (r < 0) r = 0;
        else if (r > 255) r = 255;

        if (g < 0) g = 0;
        else if (g > 255) g = 255;

        if (b < 0) b = 0;
        else if (b > 255) b = 255;

        if (a < 0) a = 0;
        else if (a > 255) a = 255;
    }

    public boolean isZero() {
        return r == 0 && g == 0 && b == 0 && a == 0;
    }

    public int getPacked() {
        return fromRGBA(r, g, b, a);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putInt("r", r);
        tag.putInt("g", g);
        tag.putInt("b", b);
        tag.putInt("a", a);

        return tag;
    }

    @Override
    public CustomColor fromTag(NbtCompound tag) {
        r = tag.getInt("r");
        g = tag.getInt("g");
        b = tag.getInt("b");
        a = tag.getInt("a");

        validate();
        return this;
    }

    @Override
    public String toString() {
        return r + " " + g + " " + b + " " + a;
    }

    public static int fromRGBA(int r, int g, int b, int a) {
        return (a << 24) + (r << 16) + (g << 8) + (b);
    }
    public static int toRGBAR(int color) {
        return (color >> 16) & 0x000000FF;
    }
    public static int toRGBAG(int color) {
        return (color >> 8) & 0x000000FF;
    }
    public static int toRGBAB(int color) {
        return (color) & 0x000000FF;
    }
    public static int toRGBAA(int color) {
        return (color >> 24) & 0x000000FF;
    }

    public static CustomColor fromHsv(double h, double s, double v) {
        double      hh, p, q, t, ff;
        int        i;
        double      r, g, b;

        if(s <= 0.0) {       // < is bogus, just shuts up warnings
            r = v;
            g = v;
            b = v;
            return new CustomColor((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
        }
        hh = h;
        if(hh >= 360.0) hh = 0.0;
        hh /= 60.0;
        i = (int) hh;
        ff = hh - i;
        p = v * (1.0 - s);
        q = v * (1.0 - (s * ff));
        t = v * (1.0 - (s * (1.0 - ff)));

        switch(i) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;

            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
            default:
                r = v;
                g = p;
                b = q;
                break;
        }
        return new CustomColor((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
    }
}
