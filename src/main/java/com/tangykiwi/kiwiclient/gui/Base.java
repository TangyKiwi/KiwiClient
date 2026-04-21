package com.tangykiwi.kiwiclient.gui;

import java.awt.Color;

import org.joml.Matrix3x2f;

import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import com.tangykiwi.kiwiclient.util.render.state.CustomQuadRenderState;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class Base extends Screen {
    public Base(Component title) {
        super(title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor drawContext, int mouseX, int mouseY, float delta) {
        Matrix3x2f matrix = new Matrix3x2f(drawContext.pose());
        ScreenRectangle scissor = drawContext.scissorStack.peek();
        if (this.minecraft.level == null) {
            int colorOffset = (int) ((System.currentTimeMillis() / 75) % 100);
            if (colorOffset > 50)
                colorOffset = 50 - (colorOffset - 50);

            // smooth
            colorOffset = (int) (-(Math.cos(Math.PI * (colorOffset / 50d)) - 1) / 2 * 50);
            
            drawContext.guiRenderState.addGuiElement(new CustomQuadRenderState(
                matrix,
                width, 0, 0, 0, 0, height + 16, width, height + 16,
                new Color(80, 53, 20). getRGB(),
                new Color(80 + colorOffset / 3, 53, 20).getRGB(),
                new Color(159, 113, 54).getRGB(),
                new Color(170 + colorOffset, 103, 45).getRGB(),
                scissor
            ));
        } else {
            drawContext.guiRenderState.addGuiElement(new CustomQuadRenderState(
                matrix,
                0, 0, 0, height, width, height, width, 0,
                -1072689136,
                -804253680,
                -804253680,
                -1072689136,
                scissor
            ));
            this.extractBlurredBackground(drawContext);
        }
    }
}
