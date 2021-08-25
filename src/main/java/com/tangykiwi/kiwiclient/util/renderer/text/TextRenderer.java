/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package com.tangykiwi.kiwiclient.util.renderer.text;

import com.tangykiwi.kiwiclient.util.CustomColor;
import com.tangykiwi.kiwiclient.util.renderer.Fonts;
import net.minecraft.client.util.math.MatrixStack;

public interface TextRenderer {
    static TextRenderer get() {
        return Fonts.CUSTOM_FONT;
    }

    void setAlpha(double a);

    void begin(double scale, boolean scaleOnly, boolean big);
    default void begin(double scale) { begin(scale, false, false); }
    default void begin() { begin(1, false, false); }

    default void beginBig() { begin(1, false, true); }

    double getWidth(String text, int length, boolean shadow);
    default double getWidth(String text, boolean shadow) { return getWidth(text, text.length(), shadow); }
    default double getWidth(String text) { return getWidth(text, text.length(), false); }

    double getHeight(boolean shadow);
    default double getHeight() { return getHeight(false); }

    double draw(MatrixStack m, String text, double x, double y, CustomColor color, boolean shadow);
    default double draw(MatrixStack m, String text, double x, double y, CustomColor color) { return draw(m, text, x, y, color, false); }

    boolean isBuilding();

    default void end() { end(null); }
    void end(MatrixStack matrices);
}
