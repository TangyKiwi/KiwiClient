package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
    private AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @ModifyReturnValue(method = "showTooltipWithItemInHand", at = @At("RETURN"))
    private boolean showTooltipWithItemInHand(boolean original, ItemStack item) {
        if (item.getTooltipImage().orElse(null) instanceof ClientTooltipComponent component) {
            return original || component.showTooltipWithItemInHand();
        }

        return original;
    }

    @Shadow
    protected int topPos;

    @Shadow
    protected int imageWidth;

    @Inject(at = @At("TAIL"), method = "init()V")
	private void onInit(CallbackInfo ci)
	{
        this.addRenderableWidget(Button.builder(Component.literal("Close w/o Packet"), (var1) -> mc.setScreenAndShow(null)).bounds(this.width / 2 - this.imageWidth / 2 - 98, this.topPos, 98, 20).build());
    }
}
