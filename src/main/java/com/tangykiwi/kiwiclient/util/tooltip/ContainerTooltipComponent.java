package com.tangykiwi.kiwiclient.util.tooltip;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import com.tangykiwi.kiwiclient.util.Textures;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.item.ItemStack;

public class ContainerTooltipComponent implements TooltipComponent, ITooltipData{
    private final ItemStack[] items;
    private final int color;

    public ContainerTooltipComponent(ItemStack[] items, int color) {
        this.items = items;
        this.color = color;
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return 67;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 176;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        // Background
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Textures.INV_BG, x, y, 0, 0, 176, 67, 176, 67, color);

        // Contents
        int row = 0;
        int i = 0;

        for (ItemStack itemStack : items) {
            context.drawItem(itemStack, x + 8 + i * 18, y + 7 + row * 18);
            context.drawStackOverlay(textRenderer, itemStack, x + 8 + i * 18, y + 7 + row * 18);

            i++;
            if (i >= 9) {
                i = 0;
                row++;
            }
        }
    }
}
