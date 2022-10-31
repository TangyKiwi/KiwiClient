package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.render.color.ColorUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class InventoryViewer extends Module {
    public InventoryViewer() {
        super("InventoryViewer", "Allows you to see what's in your inventory", KEY_UNBOUND, Category.CLIENT);
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        ItemRenderer itemRenderer = mc.getItemRenderer();
        ClientPlayerEntity player = mc.player;

        int scaledWidth = mc.getWindow().getScaledWidth();
        int scaledHeight = mc.getWindow().getScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.8F);
        RenderSystem.setShaderTexture(0, new Identifier("kiwiclient", "textures/hud/inv_bg.png"));
        mc.inGameHud.drawTexture(e.getMatrix(), scaledWidth - 164, scaledHeight - 56, 0, 0, 164, 56, 164, 56);
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                int slot = (i + 1) * 9 + j;
                ItemStack itemStack = player.getInventory().getStack(slot);
                boolean isEmpty = itemStack.isEmpty();

                if(!isEmpty) {
                    int x = scaledWidth - 164 + j * 18 + 2;
                    int y = scaledHeight - 56 + i * 18 + 2;
                    itemRenderer.renderGuiItemIcon(itemStack, x, y);
                    itemRenderer.renderGuiItemOverlay(mc.textRenderer, itemStack, x, y);
                }
            }
        }
    }
}
