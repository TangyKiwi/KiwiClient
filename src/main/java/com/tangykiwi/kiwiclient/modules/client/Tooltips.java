package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.ItemStackTooltipEvent;
import com.tangykiwi.kiwiclient.event.TooltipDataEvent;
import com.tangykiwi.kiwiclient.mixin.ContainerComponentAccessor;
import com.tangykiwi.kiwiclient.mixin.EntityAccessor;
import com.tangykiwi.kiwiclient.mixin.EntityBucketItemAccessor;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.tooltip.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BannerPatterns;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static java.awt.Color.WHITE;

public class Tooltips extends Module {

    public Tooltips() {
        super("Tooltips", "Displays even more advanced tooltips", KEY_UNBOUND, Category.CLIENT,
            new ToggleSetting("Suspicious Stew", true).withDesc("Shows effect and duration of suspicious stew"),
            new ToggleSetting("Bee Hives", true).withDesc("Shows honey level and number of bees in hive/nest"),
            new ToggleSetting("Fish", true).withDesc("Shows what mob is in a water bucket"),
            new ToggleSetting("Shulker Boxes", true).withDesc("Shows what is inside a shulkerbox when hovered over"),
            new ToggleSetting("Ender Chests", true).withDesc("Shows what is inside your enderchest when hovered over"),
            new ToggleSetting("Maps", true).withDesc("Shows the map details when hovered over").withChildren(
                new SliderSetting("Scale", 0.1, 1, 1, 1).withDesc("Scale of map")
            ),
            new ToggleSetting("Banner", true).withDesc("Shows a banner preview when hovered over"));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void appendTooltip(ItemStackTooltipEvent event) {
        // Stew
        if (getSetting(0).asToggle().state) {
            if (event.itemStack().getItem() == Items.SUSPICIOUS_STEW) {
                SuspiciousStewEffectsComponent stewEffectsComponent = event.itemStack().get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
                if (stewEffectsComponent != null) {
                    for (SuspiciousStewEffectsComponent.StewEffect effectTag : stewEffectsComponent.effects()) {
                        StatusEffectInstance effect = new StatusEffectInstance(effectTag.effect(), effectTag.duration(), 0);
                        event.list().add(1, getStatusText(effect));
                    }
                }
            } else {
                FoodComponent food = event.itemStack().get(DataComponentTypes.FOOD);
                if (food != null) {
                    food.effects().forEach(e -> event.list().add(1, getStatusText(e.effect())));
                }
            }
        }

        // Bees
        if (getSetting(1).asToggle().state) {
            if (event.itemStack().getItem() == Items.BEEHIVE || event.itemStack().getItem() == Items.BEE_NEST) {
                BlockStateComponent blockStateComponent = event.itemStack().get(DataComponentTypes.BLOCK_STATE);
                if (blockStateComponent != null) {
                    String level = blockStateComponent.properties().get("honey_level");
                    event.list().add(1, Text.literal(String.format("%sHoney level: %s%s%s.", Formatting.GRAY, Formatting.YELLOW, level, Formatting.GRAY)));
                }

                List<BeehiveBlockEntity.BeeData> bees = event.itemStack().get(DataComponentTypes.BEES);
                if (bees != null) {
                    event.list().add(1, Text.literal(String.format("%sBees: %s%d%s.", Formatting.GRAY, Formatting.YELLOW, bees.size(), Formatting.GRAY)));
                }
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void getTooltipData(TooltipDataEvent event) {

        // Shulker Box
        if (hasItems(event.itemStack) && getSetting(3).asToggle().state) {
            ItemStack[] itemStacks = new ItemStack[27];
            ItemStack itemStack = event.itemStack;
            if (itemStack.getItem() == Items.ENDER_CHEST) {
                for (int i = 0; i < EChestMemory.ITEMS.size(); i++) {
                    itemStacks[i] = EChestMemory.ITEMS.get(i);
                }

                return;
            }

            Arrays.fill(itemStacks, ItemStack.EMPTY);
            ComponentMap components = itemStack.getComponents();

            if (components.contains(DataComponentTypes.CONTAINER)) {
                ContainerComponentAccessor container = ((ContainerComponentAccessor) (Object) components.get(DataComponentTypes.CONTAINER));
                DefaultedList<ItemStack> stacks = container.getStacks();

                for (int i = 0; i < stacks.size(); i++) {
                    if (i >= 0 && i < itemStacks.length) itemStacks[i] = stacks.get(i);
                }
            }
            else if (components.contains(DataComponentTypes.BLOCK_ENTITY_DATA)) {
                NbtComponent nbt2 = components.get(DataComponentTypes.BLOCK_ENTITY_DATA);

                if (nbt2.contains("Items")) {
                    NbtList nbt3 = (NbtList) nbt2.getNbt().get("Items");

                    for (int i = 0; i < nbt3.size(); i++) {
                        int slot = nbt3.getCompound(i).getByte("Slot"); // Apparently shulker boxes can store more than 27 items, good job Mojang
                        // now NPEs when mc.world == null
                        if (slot >= 0 && slot < itemStacks.length) itemStacks[slot] = ItemStack.fromNbtOrEmpty(mc.player.getRegistryManager(), nbt3.getCompound(i));
                    }
                }
            }
            event.tooltipData = new ContainerTooltipComponent(itemStacks, getShulkerColor(event.itemStack));
        }

        // EChest
        else if (event.itemStack.getItem() == Items.ENDER_CHEST && getSetting(4).asToggle().state) {
            event.tooltipData = new ContainerTooltipComponent(EChestMemory.ITEMS.toArray(new ItemStack[27]), new Color(0, 50, 50));
        }

        // Map
        else if (event.itemStack.getItem() == Items.FILLED_MAP && getSetting(5).asToggle().state) {
            MapIdComponent mapIdComponent = event.itemStack.get(DataComponentTypes.MAP_ID);
            if (mapIdComponent != null) event.tooltipData = new MapTooltipComponent(mapIdComponent.id());
        }

        // Banner
        else if (event.itemStack.getItem() instanceof BannerItem && getSetting(6).asToggle().state) {
            event.tooltipData = new BannerTooltipComponent(event.itemStack);
        }
        else if (event.itemStack.getItem() instanceof BannerPatternItem patternItem && getSetting(6).asToggle().state) {
            BannerPatternsComponent bannerPatternsComponent = event.itemStack.get(DataComponentTypes.BANNER_PATTERNS);
            if (bannerPatternsComponent != null) {
                ItemStack bannerItem = new ItemStack(Items.GRAY_BANNER);
                BannerPatternsComponent bannerPatterns = bannerItem.get(DataComponentTypes.BANNER_PATTERNS);
                bannerPatterns.layers().addAll(bannerPatternsComponent.layers());
                bannerItem.set(DataComponentTypes.BANNER_PATTERNS, bannerPatterns);
                event.tooltipData = new BannerTooltipComponent(bannerItem);
            }
        }
        else if (event.itemStack.getItem() == Items.SHIELD && getSetting(6).asToggle().state) {
            ItemStack banner = createBannerFromShield(event.itemStack);
            if (banner != null) event.tooltipData = new BannerTooltipComponent(banner);
        }

        // Fish
        else if (event.itemStack.getItem() instanceof EntityBucketItem bucketItem && getSetting(2).asToggle().state) {
            EntityType<?> type = ((EntityBucketItemAccessor) bucketItem).getEntityType();
            Entity entity = type.create(mc.world);
            if (entity != null) {
                ((Bucketable) entity).copyDataFromNbt(event.itemStack.get(DataComponentTypes.BUCKET_ENTITY_DATA).copyNbt());
                ((EntityAccessor) entity).setInWater(true);
                event.tooltipData = new EntityTooltipComponent(entity);
            }
        }
    }

    private MutableText getStatusText(StatusEffectInstance effect) {
        MutableText text = Text.translatable(effect.getTranslationKey());
        if (effect.getAmplifier() != 0) {
            text.append(String.format(" %d (%s)", effect.getAmplifier() + 1, StatusEffectUtil.getDurationText(effect, 1, mc.world.getTickManager().getTickRate()).getString()));
        } else {
            text.append(String.format(" (%s)", StatusEffectUtil.getDurationText(effect, 1, mc.world.getTickManager().getTickRate()).getString()));
        }
        if (effect.getEffectType().value().isBeneficial()) {
            return text.formatted(Formatting.BLUE);
        } else {
            return text.formatted(Formatting.RED);
        }
    }

    public Color getShulkerColor(ItemStack shulkerItem) {
        if (shulkerItem.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block == Blocks.ENDER_CHEST) return new Color(0, 50, 50);
            if (block instanceof ShulkerBoxBlock shulkerBlock) {
                DyeColor dye = shulkerBlock.getColor();
                if (dye == null) return WHITE;
                final int color = dye.getEntityColor();
                return new Color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 1f);
            }
        }
        return WHITE;
    }

    public boolean hasItems(ItemStack itemStack) {
        ContainerComponentAccessor container = ((ContainerComponentAccessor) (Object) itemStack.get(DataComponentTypes.CONTAINER));
        if (container != null && !container.getStacks().isEmpty()) return true;

        NbtCompound compoundTag = itemStack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT).getNbt();
        return compoundTag != null && compoundTag.contains("Items", 9);
    }

    private ItemStack createBannerFromShield(ItemStack shieldItem) {
        if (!shieldItem.getComponents().isEmpty()
                || shieldItem.get(DataComponentTypes.BLOCK_ENTITY_DATA) == null
                || shieldItem.get(DataComponentTypes.BASE_COLOR) == null)
            return null;
        ItemStack bannerItem = new ItemStack(Items.GRAY_BANNER);
        BannerPatternsComponent bannerPatternsComponent = bannerItem.get(DataComponentTypes.BANNER_PATTERNS);
        BannerPatternsComponent shieldPatternsComponent = shieldItem.get(DataComponentTypes.BANNER_PATTERNS);
        if (shieldPatternsComponent == null) return bannerItem;
        bannerPatternsComponent.layers().addAll(shieldPatternsComponent.layers());
        return bannerItem;
    }
}
