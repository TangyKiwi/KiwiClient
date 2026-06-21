package com.tangykiwi.kiwiclient.util.tooltip;

import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;
import com.tangykiwi.kiwiclient.util.Textures;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;

public class ContainerTooltipComponent implements ITooltipData, ClientTooltipComponent {
    private final ItemStack[] items;
    private final int color;

    public ContainerTooltipComponent(ItemStack[] items, int color) {
        this.items = items;
        this.color = color;
    }

    @Override
    public ClientTooltipComponent getComponent() {
        return this;
    }

    @Override
    public int getHeight(Font textRenderer) {
        return 67;
    }

    @Override
    public int getWidth(Font textRenderer) {
        return 176;
    }

    @Override
    public void extractImage(Font textRenderer, int x, int y, int width, int height, GuiGraphicsExtractor context) {
        // Background
        context.blit(RenderPipelines.GUI_TEXTURED, Textures.CONTAINER, x, y, 0, 0, 176, 67, 176, 67, color);

        // Contents
        int row = 0;
        int i = 0;

        for (ItemStack itemStack : items) {
            RenderUtils.drawItem(context, itemStack, x + 8 + i * 18, y + 7 + row * 18, 1, true, null);

            i++;
            if (i >= 9) {
                i = 0;
                row++;
            }
        }
    }
}
