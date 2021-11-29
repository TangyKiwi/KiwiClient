package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.AmbientOcclusionEvent;
import com.tangykiwi.kiwiclient.event.MarkClosedEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

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
                new SliderSetting("Value", 0, 255, 64, 0).withDesc("Opacity level")));
    }

    public boolean isVisible(Block block) {
        return !isEnabled() || blocks.contains(block);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        mc.chunkCullingEnabled = false;
        mc.worldRenderer.reload();
        gamma = mc.options.gamma;
    }

    @Override
    public void onDisable() {
        mc.chunkCullingEnabled = true;
        mc.worldRenderer.reload();
        mc.options.gamma = gamma;

        super.onDisable();
    }

    @Subscribe
    public void onTick(TickEvent e) {
        mc.options.gamma = 69;
    }
}
