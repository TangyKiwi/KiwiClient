package com.tangykiwi.kiwiclient.util.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;

public class ContainerTooltipComponent implements TooltipComponent, ITooltipData {
    private static final Identifier TEXTURE_CONTAINER_BACKGROUND = new Identifier("kiwiclient", "textures/hud/container.png");

    private final DefaultedList<ItemStack> items;
    private final Color color;

    public ContainerTooltipComponent(DefaultedList<ItemStack> items, Color color) {
        this.items = items;
        this.color = color;
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

    @Override
    public int getHeight() {
        return 67;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 176;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z) {

        // Background
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        RenderSystem.setShaderTexture(0, TEXTURE_CONTAINER_BACKGROUND);
        DrawableHelper.drawTexture(matrices, x, y, z, 0, 0, 176, 67, 176, 67);

        //Contents
        int row = 0;
        int i = 0;
        for (ItemStack itemStack : items) {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();

            matrixStack.push();

            mc.getItemRenderer().renderGuiItemIcon(itemStack, x + 8 + i * 18, y + 7 + row * 18);
            mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, itemStack, x + 8 + i * 18, y + 7 + row * 18, null);

            matrixStack.pop();

            i++;
            if (i >= 9) {
                i = 0;
                row++;
            }
        }
    }
}