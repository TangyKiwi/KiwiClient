package com.tangykiwi.kiwiclient.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.util.render.color.ColorUtil;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GuiButton {

    private int index = 0;
    public int buttonId;
    public int x, y;
    public int width, heighIn;
    public String buttonText;
    private boolean movingUp;
    private Identifier icon;

    public GuiButton(int buttonId, int x, int y, int width, int heightIn, String buttonText) {
        this.buttonId = buttonId;
        this.x = x;
        this.y = y;
        this.width = width;
        this.heighIn = heightIn;
        this.buttonText = buttonText;
        movingUp = false;
        icon = new Identifier("kiwiclient:textures/menu/" + buttonText.toLowerCase() + ".png");
    }

    public void drawButton(MatrixStack matrixStack, int mouseX, int mouseY) {
        boolean hovered = mouseX >= x - width / 2 && mouseY >= y && mouseX <= x + width / 2 && mouseY <= y + 60;
        if (hovered && index < 1) {
            movingUp = true;
        } else if (index == 14 && movingUp && !hovered) {
            movingUp = false;
        }

        if (movingUp && index < 14) {
            index++;
        } else if (index > 0 && !movingUp) {
            index--;
        }

        IFont.CONSOLAS.drawCenteredString(matrixStack, buttonText, x, (int) (y + getPosition(index)) + 55, hovered ? ColorUtil.getRainbow(3, 0.8f, 1) : -1, 1);

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, icon);
        DrawableHelper.drawTexture(matrixStack, (x - width / 2), (int) (y + getPosition(index)), 0, 0, 50, 50, 50, 50);
        RenderSystem.disableBlend();
    }

    public float getPosition(int index) {
        if (movingUp) {
            return new float[]{0, -48.839F, -107.135F, -147.163F, -159.884F, -148.736F,
                -128.329F,
                -112.506F,
                -107.611F,
                -117.462F,
                -123.848F,
                -118.805F,
                -120.371F,
                -119.885F,
                -120}[index] / 10;
        } else {
            return new float[]{0,
                -0.115F,
                0.371F,
                -1.195F,
                3.848F,
                -2.538F,
                -12.389F,
                -7.494F,
                8.329F,
                28.736F,
                39.884F,
                27.163F,
                -12.865F,
                -71.161F,
                -120}[index] / 10;
        }
    }
}
