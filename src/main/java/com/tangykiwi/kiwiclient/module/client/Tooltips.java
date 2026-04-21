package com.tangykiwi.kiwiclient.module.client;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.ItemStackTooltipEvent;
import com.tangykiwi.kiwiclient.event.TooltipDataEvent;
import com.tangykiwi.kiwiclient.mixin.ContainerComponentAccessor;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.component.SuspiciousStewEffects.Entry;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class Tooltips extends Module {
    public Tooltips() {
        super("Tooltips", "Displays advanced tooltips.", Category.CLIENT,
            new ToggleSetting("Status Effects", "Shows effects and duration of status effects in items", true),
            new ToggleSetting("Bees", "Shows honey level and number of bees in hives/nests", true),
            new ToggleSetting("Shulker Boxes", "Shows contents of shulker boxes", true),
            new ToggleSetting("Ender Chests", "Shows contents of your ender chest", true),
            new ToggleSetting("Maps", "Shows a preview of maps in tooltips", true),
            new ToggleSetting("Banners", "Shows a preview of banner patterns in tooltips", true),
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
}
