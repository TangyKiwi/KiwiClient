package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.ItemStackTooltipEvent;
import com.tangykiwi.kiwiclient.event.TooltipDataEvent;
import com.tangykiwi.kiwiclient.mixin.EntityAccessor;
import com.tangykiwi.kiwiclient.mixin.EntityBucketItemAccessor;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.EntityTooltipComponent;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class Tooltips extends Module {

    public Tooltips() {
        super("Tooltips", "Displays even more advanced tooltips", KEY_UNBOUND, Category.CLIENT,
            new ToggleSetting("Suspicious Stew", true).withDesc("Shows effect and duration of suspicious stew"),
            new ToggleSetting("Bees", true).withDesc("Shows honey level and number of bees in hive/nest"),
            new ToggleSetting("Fish", true).withDesc("Shows what mob is in a water bucket")
        );
        super.toggle();
    }

    @Subscribe
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
    }

    @Subscribe
    public void getTooltipData(TooltipDataEvent event) {
        // Setting check in EntityBucketItemMixin.java
//        if (getSetting(2).asToggle().state && event.itemStack.getItem() instanceof EntityBucketItem bucketItem) {
//            EntityType<?> type = ((EntityBucketItemAccessor) bucketItem).getEntityType();
//            Entity entity = type.create(mc.world);
//            if (entity != null) {
//                ((Bucketable) entity).copyDataFromNbt(event.itemStack.getOrCreateNbt());
//                ((EntityAccessor) entity).setInWater(true);
//                event.tooltipData = new EntityTooltipComponent(entity);
//            }
//        }
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
}
