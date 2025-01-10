package com.tangykiwi.kiwiclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
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

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(width, 0, 0).color(80, 53, 20, 255);
            bufferBuilder.vertex(0, 0, 0).color(80 + colorOffset / 3, 53, 20, 255);
            bufferBuilder.vertex(0, height + 16, 0).color(159, 113, 54, 255);
            bufferBuilder.vertex(width, height + 16, 0).color(170 + colorOffset, 103, 45, 255);
            BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

            RenderSystem.disableBlend();
        }

        this.applyBlur();
        this.renderDarkening(drawContext);
    }
}
