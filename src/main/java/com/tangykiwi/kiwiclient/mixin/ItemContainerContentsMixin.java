package com.tangykiwi.kiwiclient.mixin;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.client.Tooltips;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;

@Mixin(ItemContainerContents.class)
public class ItemContainerContentsMixin {
    @Shadow
    @Final
    private List<Optional<ItemStackTemplate>> items;

    @Inject(method = "addToTooltip", at = @At("HEAD"), cancellable = true)
    private void onAddToTooltip(Item.TooltipContext context, Consumer<Component> consumer, TooltipFlag flag, DataComponentGetter components, CallbackInfo ci) {
        Tooltips tooltips = (Tooltips) KiwiClient.moduleManager.getModule(Tooltips.class);
        if (tooltips.isEnabled()) {
            if (tooltips.getSetting("Shulker Boxes").asToggle().getValue()) ci.cancel();
        }
    }
}
