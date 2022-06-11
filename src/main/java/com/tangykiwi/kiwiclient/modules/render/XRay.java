package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.RenderBlockEvent;
import com.tangykiwi.kiwiclient.event.RenderFluidEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixininterface.ISimpleOption;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FernBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.RenderLayer;

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
        new ToggleSetting("Fluids", true).withDesc("Show fluids, toggle xray to see changes"),
        new ToggleSetting("Opacity", false).withDesc("Changes opacity of non xray blocks").withChildren(
                new SliderSetting("Value", 0, 255, 128, 0).withDesc("Opacity level")));
    }

    public boolean isVisible(Block block) {
        return !isEnabled() || blocks.contains(block);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        mc.chunkCullingEnabled = false;
        mc.worldRenderer.reload();
        gamma = mc.options.getGamma().getValue();
    }

    @Override
    public void onDisable() {
        SimpleOption<Double> gammaOption = mc.options.getGamma();
        @SuppressWarnings("unchecked")
        ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
        gammaOption2.forceSetValue(gamma);
        mc.chunkCullingEnabled = true;
        mc.worldRenderer.reload();

        super.onDisable();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent e) {
        SimpleOption<Double> gammaOption = mc.options.getGamma();
        @SuppressWarnings("unchecked")
        ISimpleOption<Double> gammaOption2 = (ISimpleOption<Double>)(Object)gammaOption;
        gammaOption2.forceSetValue(16.0);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRenderBlockLight(RenderBlockEvent.Light event) {
        event.setLight(1f);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRenderBlockOpaque(RenderBlockEvent.Opaque event) {
        event.setOpaque(true);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRenderBlockDrawSide(RenderBlockEvent.ShouldDrawSide event) {
        if (blocks.contains(event.getState().getBlock())) {
            event.setDrawSide(true);
        } else if (!getSetting(1).asToggle().state) {
            event.setDrawSide(false);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRenderBlockTesselate(RenderBlockEvent.Tesselate event) {
        if (!blocks.contains(event.getState().getBlock())) {
            if(getSetting(1).asToggle().state) {
                event.getVertexConsumer().fixedColor(-1, -1, -1, getSetting(1).asToggle().getChild(0).asSlider().getValueInt());
            }
            else {
                event.setCancelled(true);
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRenderBlockLayer(RenderBlockEvent.Layer event) {
        if (getSetting(1).asToggle().state && !blocks.contains(event.getState().getBlock())) {
            event.setLayer(RenderLayer.getTranslucent());
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onRenderFluid(RenderFluidEvent event) {
        if (!getSetting(0).asToggle().state) {
            event.setCancelled(true);
        }
    }
}
