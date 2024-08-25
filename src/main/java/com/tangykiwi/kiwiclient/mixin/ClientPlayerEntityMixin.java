package com.tangykiwi.kiwiclient.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.OnMoveEvent;
import com.tangykiwi.kiwiclient.event.SendChatMessageEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixininterface.IClientPlayerEntity;
import com.tangykiwi.kiwiclient.modules.movement.SafeWalk;
import com.tangykiwi.kiwiclient.modules.render.NoPortal;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity implements IClientPlayerEntity {
    @Shadow
    protected MinecraftClient client;

    @Shadow protected void autoJump(float dx, float dz) {}

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile gameProfile)
    {
        super(world, gameProfile);
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void onMove(MovementType type, Vec3d offset, CallbackInfo callbackInfo)
    {
        OnMoveEvent event = new OnMoveEvent(type, offset);
        KiwiClient.eventBus.post(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        } else if (!type.equals(event.getType()) || !offset.equals(event.getVec())) {
            double double_1 = this.getX();
            double double_2 = this.getZ();
            super.move(event.getType(), event.getVec());
            this.autoJump((float) (this.getX() - double_1), (float) (this.getZ() - double_2));
            callbackInfo.cancel();
        }
    }

    @Override
    protected boolean clipAtLedge() {
        return super.clipAtLedge() || KiwiClient.moduleManager.getModule(SafeWalk.class).isEnabled();
    }

    @Redirect(method = "tickNausea", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;"))
    private Screen updateNauseaGetCurrentScreenProxy(MinecraftClient client) {
        if (!KiwiClient.moduleManager.getModule(NoPortal.class).isEnabled()) return null;
        return client.currentScreen;
    }
}
