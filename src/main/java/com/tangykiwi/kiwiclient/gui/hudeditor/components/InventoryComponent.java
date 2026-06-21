package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.util.Textures;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class InventoryComponent extends HUDComponent {
    public InventoryComponent(float x, float y) {
        super("Inventory", x, y);
        setHeight(56);
        setWidth(164);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);

        Player player = mc.player;
        context.blit(RenderPipelines.GUI_TEXTURED, Textures.INV_BG, (int) getX(), (int) getY(), 0, 0, 164, 56, 164, 56);

        if (mc.player != null) {
            for (int i = 0; i < 3; i++) { 
                for (int j = 0; j < 9; j++) {
                    int slot = (i + 1) * 9 + j;
                    ItemStack itemStack = player.getInventory().getItem(slot);
                    if (!itemStack.isEmpty()) {
                        RenderUtils.drawItem(context, itemStack, (int) getX() + j * 18 + 2, (int) getY() + i * 18 + 2, 1, true, null);
                    }
                }
            }
        }
    }
}
