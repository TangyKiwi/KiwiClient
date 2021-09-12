package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.ItemStackTooltipEvent;
import com.tangykiwi.kiwiclient.event.TooltipDataEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.tooltip.ContainerTooltipComponent;
import com.tangykiwi.kiwiclient.util.tooltip.EChestMemory;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class Tooltips extends Module {

    public Tooltips() {
        super("Tooltips", "Displays even more advanced tooltips", KEY_UNBOUND, Category.CLIENT,
            new ToggleSetting("Suspicious Stew", true).withDesc("Shows effect and duration of suspicious stew"),
            new ToggleSetting("Bees", true).withDesc("Shows honey level and number of bees in hive/nest"),
            new ToggleSetting("Fish", true).withDesc("Shows what mob is in a water bucket"),
            new ToggleSetting("Shulker Boxes", true).withDesc("Shows what is inside a shulkerbox when hovered over"),
            new ToggleSetting("Ender Chests", false).withDesc("Shows what is inside your enderchest when hovered over")
        );
        super.toggle();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void appendTooltip(ItemStackTooltipEvent event) {
        // Stew
        if (getSetting(0).asToggle().state) {
            if (event.itemStack.getItem() == Items.SUSPICIOUS_STEW) {
                NbtCompound tag = event.itemStack.getNbt();
                if (tag != null) {
                    NbtList effects = tag.getList("Effects", 10);
                    if (effects != null) {
                        for (int i = 0; i < effects.size(); i++) {
                            NbtCompound effectTag = effects.getCompound(i);
                            byte effectId = effectTag.getByte("EffectId");
                            int effectDuration = effectTag.contains("EffectDuration") ? effectTag.getInt("EffectDuration") : 160;
                            StatusEffectInstance effect = new StatusEffectInstance(StatusEffect.byRawId(effectId), effectDuration, 0);
                            event.list.add(1, getStatusText(effect));
                        }
                    }
                }
            }
            else if (event.itemStack.getItem().isFood()) {
                FoodComponent food = event.itemStack.getItem().getFoodComponent();
                if (food != null) {
                    food.getStatusEffects().forEach((e) -> {
                        StatusEffectInstance effect = e.getFirst();
                        event.list.add(1, getStatusText(effect));
                    });
                }
            }
        }

        // Bees
        if (getSetting(1).asToggle().state) {
            if (event.itemStack.getItem() == Items.BEEHIVE || event.itemStack.getItem() == Items.BEE_NEST) {
                NbtCompound tag = event.itemStack.getNbt();
                if (tag != null) {
                    NbtCompound blockStateTag = tag.getCompound("BlockStateTag");
                    if (blockStateTag != null) {
                        int level = blockStateTag.getInt("honey_level");
                        event.list.add(1, new LiteralText(String.format("%sHoney Level: %s%d%s",
                                Formatting.GRAY, Formatting.YELLOW, level, Formatting.GRAY)));
                    }
                    NbtCompound blockEntityTag = tag.getCompound("BlockEntityTag");
                    if (blockEntityTag != null) {
                        NbtList beesTag = blockEntityTag.getList("Bees", 10);
                        event.list.add(1, new LiteralText(String.format("%sBees: %s%d%s",
                                Formatting.GRAY, Formatting.YELLOW, beesTag.size(), Formatting.GRAY)));
                    }
                }
            }
        }

        // Fish handled in EntityBucketItemMixin
    }

    @Subscribe
    @AllowConcurrentEvents
    public void getTooltipData(TooltipDataEvent event) {

        // Shulker Box
        if (hasItems(event.itemStack) && getSetting(3).asToggle().state) {
            NbtCompound compoundTag = event.itemStack.getSubNbt("BlockEntityTag");
            DefaultedList<ItemStack> itemStacks = DefaultedList.ofSize(27, ItemStack.EMPTY);
            Inventories.readNbt(compoundTag, itemStacks);
            event.tooltipData = new ContainerTooltipComponent(itemStacks, getShulkerColor(event.itemStack));
        }

        // EChest
        else if (event.itemStack.getItem() == Items.ENDER_CHEST && getSetting(4).asToggle().state) {
            event.tooltipData = new ContainerTooltipComponent(EChestMemory.ITEMS, new Color(0, 50, 50));
        }
    }

    private MutableText getStatusText(StatusEffectInstance effect) {
        MutableText text = new TranslatableText(effect.getTranslationKey());
        if (effect.getAmplifier() != 0) {
            text.append(String.format(" %d (%s)", effect.getAmplifier() + 1, StatusEffectUtil.durationToString(effect, 1)));
        } else {
            text.append(String.format(" (%s)", StatusEffectUtil.durationToString(effect, 1)));
        }
        if (effect.getEffectType().isBeneficial()) {
            return text.formatted(Formatting.BLUE);
        } else {
            return text.formatted(Formatting.RED);
        }
    }

    public Color getShulkerColor(ItemStack shulkerItem) {
        if (!(shulkerItem.getItem() instanceof BlockItem)) return Color.WHITE;
        Block block = ((BlockItem) shulkerItem.getItem()).getBlock();
        if (block == Blocks.ENDER_CHEST) return new Color(0, 50, 50);
        if (!(block instanceof ShulkerBoxBlock)) return Color.WHITE;
        ShulkerBoxBlock shulkerBlock = (ShulkerBoxBlock) ShulkerBoxBlock.getBlockFromItem(shulkerItem.getItem());
        DyeColor dye = shulkerBlock.getColor();
        if (dye == null) return Color.WHITE;
        final float[] colors = dye.getColorComponents();
        return new Color(colors[0], colors[1], colors[2], 1f);
    }

    public boolean hasItems(ItemStack itemStack) {
        NbtCompound compoundTag = itemStack.getSubNbt("BlockEntityTag");
        return compoundTag != null && compoundTag.contains("Items", 9);
    }
}
