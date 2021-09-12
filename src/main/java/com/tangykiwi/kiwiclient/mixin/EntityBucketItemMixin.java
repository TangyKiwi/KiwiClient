package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.client.Tooltips;
import com.tangykiwi.kiwiclient.util.tooltip.EntityTooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.EntityType;
import net.minecraft.item.EntityBucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(EntityBucketItem.class)
public abstract class EntityBucketItemMixin extends Item {
    @Shadow
    @Final
    private EntityType<?> entityType;

    public EntityBucketItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if(KiwiClient.moduleManager.getModule(Tooltips.class).isEnabled() && KiwiClient.moduleManager.getModule(Tooltips.class).getSetting(2).asToggle().state) return EntityTooltipComponent.of(this.entityType, stack.getOrCreateNbt()).or(() -> super.getTooltipData(stack));
        return super.getTooltipData(stack);
    }
}