package com.tangykiwi.kiwiclient.gui.window.widget;

import com.tangykiwi.kiwiclient.gui.window.Window;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

public class WindowButtonWidget extends WindowWidget {

    public int colorTop;
    public int colorBottom;
    public int colorFill;
    public int colorHoverFill;

    public String text;

    public int textColor;
    public Runnable action;

    public WindowButtonWidget(int x1, int y1, int x2, int y2, String text, Runnable action, int textColor) {
        this(x1, y1, x2, y2, 0x60606090, 0x4fb070f0, text, action, textColor);
    }

    public WindowButtonWidget(int x1, int y1, int x2, int y2, int colorFill, int colorHoverFill, String text, Runnable action, int textColor) {
        this(x1, y1, x2, y2, 0xff6060b0, 0xff8070b0, colorFill, colorHoverFill, text, action, textColor);
    }

    public WindowButtonWidget(int x1, int y1, int x2, int y2, int colorTop, int colorBottom, int colorFill, int colorHoverFill, String text, Runnable action, int textColor) {
        super(x1, y1, x2, y2);
        this.colorTop = colorTop;
        this.colorBottom = colorBottom;
        this.colorFill = colorFill;
        this.colorHoverFill = colorHoverFill;
        this.text = text;
        this.action = action;
        this.textColor = textColor;
    }

    @Override
    public void render(DrawContext context, int windowX, int windowY, int mouseX, int mouseY) {
        super.render(context, windowX, windowY, mouseX, mouseY);

        int bx1 = windowX + x1;
        int by1 = windowY + y1;
        int bx2 = windowX + x2;
        int by2 = windowY + y2;

        Window.fill(context,
                bx1, by1, bx2, by2,
                colorTop, colorBottom,
                isInBounds(windowX, windowY, mouseX, mouseY) ? colorHoverFill : colorFill);

        IFont.CONSOLAS.drawCenteredStringWithShadow(context.getMatrices(), text, (float) (bx1 + (bx2 - bx1) / 2) - 1.2, (float) (by1 + (by2 - by1) / 2 - 4), textColor, 1.0F);
//		mc.textRenderer.drawWithShadow(
//				matrices, text, bx1 + (bx2 - bx1) / 2 - mc.textRenderer.getWidth(text) / 2, by1 + (by2 - by1) / 2 - 4, -1);
    }

    @Override
    public void mouseClicked(int windowX, int windowY, int mouseX, int mouseY, int button) {
        super.mouseClicked(windowX, windowY, mouseX, mouseY, button);

        if (isInBounds(windowX, windowY, mouseX, mouseY)) {
            action.run();
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }
}