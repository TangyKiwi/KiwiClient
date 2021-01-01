package com.tangykiwi.kiwiclient.modules.player;

import com.google.common.eventbus.Subscribe;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.gui.Window;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class InventoryViewer extends Module {
    public InventoryViewer() {
        super("InventoryViewer", "Allows you to see what's in your inventory", KEY_UNBOUND, Category.PLAYER);
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        TextureManager textureManager = mc.getTextureManager();
        textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);

        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        ItemRenderer itemRenderer = mc.getItemRenderer();
        ClientPlayerEntity player = mc.player;

        int scaledWidth = mc.getWindow().getScaledWidth();
        int scaledHeight = mc.getWindow().getScaledHeight();

        ColorUtil.fillGradient(new MatrixStack(), scaledWidth - (9 * 18 + 4), scaledHeight - (3 * 18 + 4), scaledWidth, scaledHeight, -1072689136, -804253680);

        for(int i = 1; i <= 4; i++) {
            for(int j = 0; j < 9; j++) {
                int slot = i * 9 + j;
                ItemStack itemStack = player.inventory.getStack(slot);
                boolean isEmpty = itemStack.isEmpty();

                if(!isEmpty) {
                    int x = scaledWidth - (9 * 18 + 4) + j * 18 + 2;
                    int y = scaledHeight - (3 * 18 + 4) + (i - 1) * 18 + 2;
                    itemRenderer.renderGuiItemIcon(itemStack, x, y);
                    itemRenderer.renderGuiItemOverlay(mc.textRenderer, itemStack, x, y);
                }
            }
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
    }
}
