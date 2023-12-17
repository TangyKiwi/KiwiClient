package com.tangykiwi.kiwiclient.gui.window.widget;

import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class WindowTextWidget extends WindowWidget {

    private String text;
    private float scale;
    public boolean shadow;
    public int color;
    public TextAlign align;
    public float rotation;

    public WindowTextWidget(String text, boolean shadow, int x, int y, int color) {
        this(text, shadow, TextAlign.LEFT, 1f, 0f, x, y, color);
    }
    public WindowTextWidget(String text, boolean shadow, TextAlign align, float scale, float rotation, int x, int y, int color) {
        super(x, y, x + mc.textRenderer.getWidth(text), (int) (y + 10 * scale));
        this.text = text;
        this.shadow = shadow;
        this.color = color;
        this.align = align;
        this.scale = scale;
        this.rotation = rotation;
    }

    @Override
    public void render(DrawContext context, int windowX, int windowY, int mouseX, int mouseY) {
        super.render(context, windowX, windowY, mouseX, mouseY);

        MatrixStack matrices = context.getMatrices();

        float offset = IFont.CONSOLAS.getStringWidth(text) * align.offset * scale;

        matrices.push();
        matrices.scale(scale, scale, 1f);
        matrices.translate((windowX + x1 - offset) / scale, (windowY + y1) / scale, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));

        IFont.CONSOLAS.drawString(matrices, text, 0, 0, color, scale);

        matrices.pop();
    }

    public String getText() {
        return text;
    }

    public void setText(String text, int color) {
        this.text = text;
        this.x2 = x1 + IFont.CONSOLAS.getStringWidth(text);
        this.color = color;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.x2 = (int) (x1 + mc.textRenderer.getWidth(text) * scale);
        this.y2 = (int) (y1 + 10 * scale);
    }

    public enum TextAlign {
        LEFT(0f),
        MIDDLE(0.5f),
        RIGHT(1f);

        public final float offset;

        TextAlign(float offset) {
            this.offset = offset;
        }
    }

}
