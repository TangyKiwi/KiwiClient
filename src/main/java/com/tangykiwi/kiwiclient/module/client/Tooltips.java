package com.tangykiwi.kiwiclient.module.client;

import com.google.common.eventbus.Subscribe;
import com.mojang.serialization.DataResult;
import com.tangykiwi.kiwiclient.event.ItemStackTooltipEvent;
import com.tangykiwi.kiwiclient.event.TooltipDataEvent;
import com.tangykiwi.kiwiclient.mixin.EntityAccessor;
import com.tangykiwi.kiwiclient.mixin.MobBucketItemAccessor;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.util.tooltip.BannerTooltipComponent;
import com.tangykiwi.kiwiclient.util.tooltip.ContainerTooltipComponent;
import com.tangykiwi.kiwiclient.util.tooltip.EntityTooltipComponent;
import com.tangykiwi.kiwiclient.util.tooltip.MapTooltipComponent;

import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.component.SuspiciousStewEffects.Entry;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.saveddata.maps.MapId;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.Arrays;
import java.util.Optional;

public class Tooltips extends Module {
    private static final ItemStack[] PREVIEW = new ItemStack[27];

    public Tooltips() {
        super("Tooltips", "Displays advanced tooltips.", Category.CLIENT,
            new ToggleSetting("Status Effects", "Shows effects and duration of status effects in items", true),
            new ToggleSetting("Bees", "Shows honey level and number of bees in hives/nests", true),
            new ToggleSetting("Shulker Boxes", "Shows contents of shulker boxes", true),
            new ToggleSetting("Ender Chest", "Shows contents of your ender chest", true),
            new ToggleSetting("Maps", "Shows a preview of maps in tooltips", true),
            new ToggleSetting("Banners", "Shows a preview of banner patterns in tooltips", true).withChildren(
                new SliderSetting("Scale", "Map preview scale", 0.1, 1, 1, 1)
            ),
            new ToggleSetting("Fish", "Shows mob in water bucket", true)
        );
    }

    @Subscribe
    public void appendTooltip(ItemStackTooltipEvent event) {
        if (getSetting("Status Effects").asToggle().getValue()) {
            if (event.itemStack().getItem() == Items.SUSPICIOUS_STEW) {
                SuspiciousStewEffects stewEffectsComponent = event.itemStack().get(DataComponents.SUSPICIOUS_STEW_EFFECTS);
                if (stewEffectsComponent != null) {
                    for (Entry effectTag : stewEffectsComponent.effects()) {
                        MobEffectInstance effect = new MobEffectInstance(effectTag.effect(), effectTag.duration(), 0);
                        event.appendStart(getStatusText(effect));
                    }
                }
            } else {
                Consumable consumable = event.itemStack().get(DataComponents.CONSUMABLE);
                if (consumable != null) {
                    consumable.onConsumeEffects().stream()
                        .filter(ApplyStatusEffectsConsumeEffect.class::isInstance)
                        .map(ApplyStatusEffectsConsumeEffect.class::cast)
                        .flatMap(apply -> apply.effects().stream())
                        .forEach(effect -> event.appendStart(getStatusText(effect)));
                }
            }
        }

        if (getSetting("Bees").asToggle().getValue()) {
            if (event.itemStack().getItem() == Items.BEEHIVE || event.itemStack().getItem() == Items.BEE_NEST) {
                BlockItemStateProperties blockStateComponent = event.itemStack().get(DataComponents.BLOCK_STATE);
                if (blockStateComponent != null) {
                    String level = blockStateComponent.properties().get("honey_level");
                    event.append(1, Component.literal(String.format("%sHoney level: %s%s%s.", ChatFormatting.GRAY, ChatFormatting.YELLOW, level, ChatFormatting.GRAY)));
                }

                Bees bees = event.itemStack().get(DataComponents.BEES);
                if (bees != null) {
                    event.append(1, Component.literal(String.format("%sBees: %s%d%s.", ChatFormatting.GRAY, ChatFormatting.YELLOW, bees.bees().size(), ChatFormatting.GRAY)));
                }
            }
        }
    }

    private MutableComponent getStatusText(MobEffectInstance effect) {
        MutableComponent text = Component.translatable(effect.getDescriptionId());
        if (effect.getAmplifier() != 0) {
            text.append(String.format(" %d (%s)", effect.getAmplifier() + 1, MobEffectUtil.formatDuration(effect, 1, mc.level.tickRateManager().tickrate()).getString()));
        } else {
            text.append(String.format(" (%s)", MobEffectUtil.formatDuration(effect, 1, mc.level.tickRateManager().tickrate()).getString()));
        }

        if (effect.getEffect().value().isBeneficial()) return text.withStyle(ChatFormatting.BLUE);
        return text.withStyle(ChatFormatting.RED);
    }

    @Subscribe
    public void appendTooltip(TooltipDataEvent event) {
        if (getSetting("Shulker Boxes").asToggle().getValue()) {
            getItems(event.itemStack, PREVIEW);
            event.tooltipData = new ContainerTooltipComponent(PREVIEW, getShulkerColor(event.itemStack));
        }

        else if (event.itemStack.getItem() == Items.ENDER_CHEST && getSetting("Ender Chest").asToggle().getValue()) {
            // handle echest memory
        }

        else if (event.itemStack.getItem() == Items.FILLED_MAP && getSetting("Maps").asToggle().getValue()) {
            MapId mapId = event.itemStack.get(DataComponents.MAP_ID);
            if (mapId != null) event.tooltipData = new MapTooltipComponent(mapId.id());
        }

        else if (event.itemStack.getItem() instanceof BannerItem && getSetting("Banners").asToggle().getValue()) {
            event.tooltipData = new BannerTooltipComponent(event.itemStack);
        } else if (event.itemStack.has(DataComponents.PROVIDES_BANNER_PATTERNS) && getSetting("Banners").asToggle().getValue()) {
            event.tooltipData = createBannerFromBannerPatternItem(event.itemStack);
        } else if (event.itemStack.getItem() == Items.SHIELD && getSetting("Banners").asToggle().getValue()) {
            if (!event.itemStack.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY).layers().isEmpty()) {
                event.tooltipData = createBannerFromShield(event.itemStack);
            }
        }

        else if (event.itemStack.getItem() instanceof MobBucketItem bucketItem && getSetting("Fish").asToggle().getValue()) {
            EntityType<?> type = ((MobBucketItemAccessor) bucketItem).getType();
            LivingEntity entity = (LivingEntity) type.create(mc.level, EntitySpawnReason.NATURAL);

            if (entity != null) {
                CustomData nbtComponent = event.itemStack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, null);
                if (nbtComponent == null) {
                    return;
                }

                entity.applyComponentsFromItemStack(event.itemStack);
                ((Bucketable) entity).loadFromBucketTag(nbtComponent.copyTag());
                ((EntityAccessor) entity).setInWater(true);
                event.tooltipData = new EntityTooltipComponent(entity);
            }
        }
    }

    public static boolean hasItems(ItemStack itemStack) {
        var container = itemStack.get(DataComponents.CONTAINER);

        if (container != null) {
            var items = container.nonEmptyItems().iterator();
            if (items.hasNext()) return true;
        }

        TypedEntityData<BlockEntityType<?>> blockEntityData = itemStack.get(DataComponents.BLOCK_ENTITY_DATA);
        return blockEntityData != null && blockEntityData.contains("Items");
    }

    public static void getItems(ItemStack itemStack, ItemStack[] items) {
        // handle echest memory
        if (itemStack.getItem() == Items.ENDER_CHEST) {
            for (int i = 0; i < 27; i++) {

            }

            return;
        }

        Arrays.fill(items, ItemStack.EMPTY);
        DataComponentMap components = itemStack.getComponents();

        if (components.has(DataComponents.CONTAINER)) {
            var stacks = components.get(DataComponents.CONTAINER).allItemsCopyStream().toList();

            for (int i = 0; i < stacks.size(); i++) {
                if (i < items.length) {
                    items[i] = stacks.get(i);
                }
            }
        } else if (components.has(DataComponents.BLOCK_ENTITY_DATA)) {
            TypedEntityData<BlockEntityType<?>> blockEntityData = components.get(DataComponents.BLOCK_ENTITY_DATA);
            if (blockEntityData == null) return;
            ListTag nbt3 = blockEntityData.copyTagWithoutId().getListOrEmpty("Items");

            for (int i = 0; i < nbt3.size(); i++) {
                Optional<CompoundTag> compound = nbt3.getCompound(i);
                if (compound.isEmpty()) continue;

                Optional<Byte> slot = compound.get().getByte("Slot"); // Apparently shulker boxes can store more than 27 items, good job Mojang
                if (slot.isEmpty()) continue;

                // now NPEs when mc.world == null
                if (slot.get() >= 0 && slot.get() < items.length) {
                    switch (ItemStackWithSlot.CODEC.parse(mc.player.registryAccess().createSerializationContext(NbtOps.INSTANCE), compound.get())) {
                        case DataResult.Success<ItemStackWithSlot> success ->
                            items[slot.get()] = success.value().stack();
                        case DataResult.Error<ItemStackWithSlot> _ -> items[slot.get()] = ItemStack.EMPTY;
                        default -> throw new MatchException(null, null);
                    }
                }
            }
        }
    }

    public static int getShulkerColor(ItemStack shulkerItem) {
        if (shulkerItem.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block == Blocks.ENDER_CHEST) return 0x003232;

            if (block instanceof ShulkerBoxBlock shulkerBlock) {
                DyeColor dye = shulkerBlock.getColor();
                if (dye == null) return 0xFFFFFF;

                return dye.getTextureDiffuseColor();
            }
        }

        return 0xFFFFFF;
    }

    private BannerTooltipComponent createBannerFromBannerPatternItem(ItemStack item) {
        HolderSet<BannerPattern> providedPatterns = item.get(DataComponents.PROVIDES_BANNER_PATTERNS);
        if (providedPatterns == null || providedPatterns.size() == 0) {
            return new BannerTooltipComponent(DyeColor.GRAY, BannerPatternLayers.EMPTY);
        }

        BannerPatternLayers component = new BannerPatternLayers.Builder().add(providedPatterns.get(0), DyeColor.WHITE).build();
        return new BannerTooltipComponent(DyeColor.GRAY, component);
    }

    private BannerTooltipComponent createBannerFromShield(ItemStack shieldItem) {
        DyeColor dyeColor2 = shieldItem.getOrDefault(DataComponents.BASE_COLOR, DyeColor.WHITE);
        BannerPatternLayers bannerPatternsComponent = shieldItem.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
        return new BannerTooltipComponent(dyeColor2, bannerPatternsComponent);
    }
}
