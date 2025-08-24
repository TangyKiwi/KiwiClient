package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.util.Textures;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public class InventoryComponent extends HUDComponent {
    public InventoryComponent(float x, float y) {
        super("Inventory", x, y);
        setHeight(56);
        setWidth(164);
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);

        ClientPlayerEntity player = mc.player;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Textures.INV_BG, (int) getX(), (int) getY(), 0, 0, 164, 56, 164, 56);

        if (mc.player != null) {
            for (int i = 0; i < 3; i++) { 
                for (int j = 0; j < 9; j++) {
                    int slot = (i + 1) * 9 + j;
                    ItemStack itemStack = player.getInventory().getStack(slot);
                    if (!itemStack.isEmpty()) {
                        context.drawItem(itemStack, (int) getX() + j * 18 + 2, (int) getY() + i * 18 + 2);
                        context.drawStackOverlay(mc.textRenderer, itemStack, (int) getX() + j * 18 + 2, (int) getY() + i * 18 + 2);
                    }
                }
            }
        }
    }
}
