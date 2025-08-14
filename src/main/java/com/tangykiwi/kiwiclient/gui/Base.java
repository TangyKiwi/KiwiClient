package com.tangykiwi.kiwiclient.gui;

import java.awt.Color;

import org.joml.Matrix3x2f;

import com.tangykiwi.kiwiclient.util.render.CustomQuadRenderState;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class Base extends Screen {
    public Base(Text title) {
        super(title);
    }

    @Override
    public void renderBackground(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (this.client.world == null) {
            int colorOffset = (int) ((System.currentTimeMillis() / 75) % 100);
            if (colorOffset > 50)
                colorOffset = 50 - (colorOffset - 50);

            // smooth
            colorOffset = (int) (-(Math.cos(Math.PI * (colorOffset / 50d)) - 1) / 2 * 50);

            Matrix3x2f matrix = new Matrix3x2f(drawContext.getMatrices());
            ScreenRect scissor = drawContext.scissorStack.peekLast();
            drawContext.state.addSimpleElement(new CustomQuadRenderState(
                matrix,
                width, 0, 0, 0, 0, height + 16, width, height + 16,
                new Color(80, 53, 20). getRGB(),
                new Color(80 + colorOffset / 3, 53, 20).getRGB(),
                new Color(159, 113, 54).getRGB(),
                new Color(170 + colorOffset, 103, 45).getRGB(),
                scissor
            ));
        } else {
            RenderUtils.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
            this.applyBlur(drawContext);
        }
    }
}
