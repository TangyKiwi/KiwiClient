package com.tangykiwi.kiwiclient.module.other;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class DummyModule extends Module {
    public DummyModule() {
        super("DummyModule", "Dummy Module", -1, Category.OTHER);
    }

    ItemStack item = Items.DIAMOND_HELMET.getDefaultStack();

    @Subscribe
    public void onWorldRender(WorldRenderEvent.Post event) {
        RenderUtils.drawBoxOutline(event.getMatrixStack(), mc.player.getBlockPos(), 0xFFFF0000, 4);
    }
}
