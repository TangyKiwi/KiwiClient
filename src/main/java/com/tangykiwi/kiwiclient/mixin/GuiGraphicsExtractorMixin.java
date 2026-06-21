package com.tangykiwi.kiwiclient.mixin;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import com.tangykiwi.kiwiclient.mixininterface.ITooltipData;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {
    @Inject(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V", shift = At.Shift.BEFORE))
    private void onDrawTooltip(Font font, List<Component> texts, Optional<TooltipComponent> optionalImage, int xo, int yo, @Nullable Identifier style, CallbackInfo ci, @Local(name = "components") List<ClientTooltipComponent> components) {
        if (optionalImage.isPresent() && optionalImage.get() instanceof ITooltipData iTooltipData) {
            components.add(iTooltipData.getComponent());
        }
    }

    @ModifyReceiver(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private Optional<TooltipComponent> onDrawTooltip_modifyIfPresentReceiver(Optional<TooltipComponent> data, Consumer<TooltipComponent> action) {
        if (data.isPresent() && data.get() instanceof ITooltipData) {
            return Optional.empty();
        }

        return data;
    }
}
