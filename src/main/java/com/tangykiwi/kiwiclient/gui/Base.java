package com.tangykiwi.kiwiclient.gui;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.tangykiwi.kiwiclient.util.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
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

            RenderUtils.setupRender();  

            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(width, 0, 0).color(80, 53, 20, 255);
            bufferBuilder.vertex(0, 0, 0).color(80 + colorOffset / 3, 53, 20, 255);
            bufferBuilder.vertex(0, height + 16, 0).color(159, 113, 54, 255);
            bufferBuilder.vertex(width, height + 16, 0).color(170 + colorOffset, 103, 45, 255);
            RenderLayer.getLines().draw(bufferBuilder.end());

            RenderUtils.endRender();
        } else {
            RenderUtils.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
            this.applyBlur(drawContext);
        }
    }
}
