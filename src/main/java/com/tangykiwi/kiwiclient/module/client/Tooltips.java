package com.tangykiwi.kiwiclient.module.client;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.ItemStackTooltipEvent;
import com.tangykiwi.kiwiclient.event.TooltipDataEvent;
import com.tangykiwi.kiwiclient.mixin.ContainerComponentAccessor;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BeesComponent;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent.StewEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
                SuspiciousStewEffectsComponent stewEffectsComponent = event.itemStack().get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
                if (stewEffectsComponent != null) {
                    for (StewEffect effectTag : stewEffectsComponent.effects()) {
                        StatusEffectInstance effect = new StatusEffectInstance(effectTag.effect(), effectTag.duration(), 0);
                        event.appendStart(getStatusText(effect));
                    }
                }
            } else {
                ConsumableComponent consumable = event.itemStack().get(DataComponentTypes.CONSUMABLE);
                if (consumable != null) {
                    consumable.onConsumeEffects().stream()
                        .filter(ApplyEffectsConsumeEffect.class::isInstance)
                        .map(ApplyEffectsConsumeEffect.class::cast)
                        .flatMap(apply -> apply.effects().stream())
                        .forEach(effect -> event.appendStart(getStatusText(effect)));
                }
            }
        }

        if (getSetting("Bees").asToggle().getValue()) {
            if (event.itemStack().getItem() == Items.BEEHIVE || event.itemStack().getItem() == Items.BEE_NEST) {
                BlockStateComponent blockStateComponent = event.itemStack().get(DataComponentTypes.BLOCK_STATE);
                if (blockStateComponent != null) {
                    String level = blockStateComponent.properties().get("honey_level");
                    event.list().add(1, Text.literal(String.format("%sHoney level: %s%s%s.", Formatting.GRAY, Formatting.YELLOW, level, Formatting.GRAY)));
                }

                BeesComponent bees = event.itemStack().get(DataComponentTypes.BEES);
                if (bees != null) {
                    event.list().add(1, Text.literal(String.format("%sBees: %s%d%s.", Formatting.GRAY, Formatting.YELLOW, bees.bees().size(), Formatting.GRAY)));
                }
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

        if (effect.getEffectType().value().isBeneficial()) return text.formatted(Formatting.BLUE);
        return text.formatted(Formatting.RED);
    }

    @Subscribe
    public void getTooltipData(TooltipDataEvent event) {
        
    }

    public static boolean hasItems(ItemStack itemStack) {
        ContainerComponentAccessor container = ((ContainerComponentAccessor) (Object) itemStack.get(DataComponentTypes.CONTAINER));
        if (container != null && !container.getStacks().isEmpty()) return true;

        NbtComponent blockEntityData = itemStack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        return blockEntityData != null && blockEntityData.contains("Items");
    }
}
