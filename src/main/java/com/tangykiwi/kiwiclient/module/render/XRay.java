package com.tangykiwi.kiwiclient.module.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixininterface.ISimpleOption;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.client.OptionInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.ArrayList;
import java.util.Arrays;

public class XRay extends Module {

    private double gamma;
    private ArrayList<Block> blocks = new ArrayList<Block>(Arrays.asList(
        Blocks.COAL_ORE,
        Blocks.DEEPSLATE_COAL_ORE,
        Blocks.COAL_BLOCK,
        Blocks.COPPER_ORE,
        Blocks.DEEPSLATE_COPPER_ORE,
        Blocks.COPPER_BLOCK,
        Blocks.IRON_ORE,
        Blocks.DEEPSLATE_IRON_ORE,
        Blocks.IRON_BLOCK,
        Blocks.RAW_IRON_BLOCK,
        Blocks.GOLD_ORE,
        Blocks.DEEPSLATE_GOLD_ORE,
        Blocks.GOLD_BLOCK,
        Blocks.RAW_GOLD_BLOCK,
        Blocks.LAPIS_ORE,
        Blocks.DEEPSLATE_LAPIS_ORE,
        Blocks.LAPIS_BLOCK,
        Blocks.REDSTONE_ORE,
        Blocks.DEEPSLATE_REDSTONE_ORE,
        Blocks.REDSTONE_BLOCK,
        Blocks.DIAMOND_ORE,
        Blocks.DEEPSLATE_DIAMOND_ORE,
        Blocks.DIAMOND_BLOCK,
        Blocks.NETHER_GOLD_ORE,
        Blocks.ANCIENT_DEBRIS,
        Blocks.NETHERITE_BLOCK,
        Blocks.SPAWNER,
        Blocks.END_PORTAL_FRAME
    ));

    public XRay() {
        super("XRay", "Shows ores", KEY_UNBOUND, Category.RENDER,
        new ToggleSetting("Fluids", "Show fluids, toggle xray to see changes", true),
        new ToggleSetting("Opacity", "Changes opacity of non xray blocks", false).withChildren(
                new SliderSetting("Value", "Opacity level", 0, 255, 128, 0)));
    }

    public boolean isVisible(Block block) {
        return !isEnabled() || blocks.contains(block);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        mc.levelRenderer.allChanged();
        gamma = mc.options.gamma().get();
    }

    @Override
    public void onDisable() {
        OptionInstance<Double> gammaOption = mc.options.gamma();
        @SuppressWarnings("unchecked")
        ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
        gammaOption2.forceSetValue(gamma);
        mc.levelRenderer.allChanged();

        super.onDisable();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent e) {
        OptionInstance<Double> gammaOption = mc.options.gamma();
        @SuppressWarnings("unchecked")
        ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
        gammaOption2.forceSetValue(16.0);
    }

    // @Subscribe
    // @AllowConcurrentEvents
    // public void onRenderBlockLight(RenderBlockEvent.Light event) {
    //     event.setLight(1f);
    // }

    // @Subscribe
    // @AllowConcurrentEvents
    // public void onRenderBlockOpaque(RenderBlockEvent.Opaque event) {
    //     event.setOpaque(true);
    // }

    // @Subscribe
    // @AllowConcurrentEvents
    // public void onRenderBlockDrawSide(RenderBlockEvent.ShouldDrawSide event) {
    //     if (blocks.contains(event.getState().getBlock())) {
    //         event.setDrawSide(true);
    //     } else if (!getSetting(1).asToggle().getValue()) {
    //         event.setDrawSide(false);
    //     }
    // }

    // @Subscribe
    // @AllowConcurrentEvents
    // public void onRenderBlockTesselate(RenderBlockEvent.Tesselate event) {
    //     if (!blocks.contains(event.getState().getBlock())) {
    //         if(getSetting(1).asToggle().getValue()) {
    //             event.getVertexConsumer().color(-1, -1, -1, getSetting(1).asToggle().getChild(0).asSlider().getValueInt());
    //         }
    //         else {
    //             event.setCancelled(true);
    //         }
    //     }
    // }

    // @Subscribe
    // @AllowConcurrentEvents
    // public void onRenderBlockLayer(RenderBlockEvent.Layer event) {
    //     if (getSetting(1).asToggle().getValue() && !blocks.contains(event.getState().getBlock())) {
    //         event.setLayer(BlockRenderLayer.TRANSLUCENT);
    //     }
    // }

    // @Subscribe
    // @AllowConcurrentEvents
    // public void onRenderFluid(RenderFluidEvent event) {
    //     if (!getSetting(0).asToggle().getValue()) {
    //         event.setCancelled(true);
    //     }
    // }
}
