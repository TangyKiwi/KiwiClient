package com.tangykiwi.kiwiclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.PlayerMoveEvent;
import com.tangykiwi.kiwiclient.event.SendMovementPacketEvent;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends AbstractClientPlayer{
    public LocalPlayerMixin(ClientLevel level, ClientPacketListener connection) {
        super(level, connection.getLocalGameProfile());
    }

    @Inject(method = "sendPosition", at = @At("HEAD"))
    private void onSendMovementPacketsHead(CallbackInfo info) {
        KiwiClient.eventBus.post(SendMovementPacketEvent.Pre.get());
    }

    @Inject(method = "sendPosition", at = @At("TAIL"))
    private void onSendMovementPacketsTail(CallbackInfo info) {
        KiwiClient.eventBus.post(SendMovementPacketEvent.Post.get());
    }

    @Shadow
    protected void updateAutoJump(final float xa, final float za) {}

    @Inject(method = "move", at = @At("HEAD"))
    private void onMove(MoverType moverType, Vec3 delta, CallbackInfo ci) {
        PlayerMoveEvent event = new PlayerMoveEvent(moverType, delta);
        KiwiClient.eventBus.post(event);

        if (event.isCancelled()) {
            ci.cancel();
        } else if (!moverType.equals(event.getMoverType()) || !delta.equals(event.getVec())) {
            double double_1 = this.getX();
            double double_2 = this.getZ();
            super.move(event.getMoverType(), event.getVec());
            this.updateAutoJump((float) (this.getX() - double_1), (float) (this.getZ() - double_2));
            ci.cancel();
        }
    }
}
