package com.tangykiwi.kiwiclient.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.command.CommandManager;
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
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
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

    @Shadow
    protected abstract boolean isCamera();

    private boolean ignoreChatMessage;

    @Shadow public abstract void sendChatMessage(String string);

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile gameProfile)
    {
        super(world, gameProfile);
    }

    @Inject(method = "tick()V", at = @At("RETURN"), cancellable = true)
    public void tick(CallbackInfo info) {
        TickEvent event = new TickEvent();
        KiwiClient.eventBus.post(event);
        if (event.isCancelled())
            info.cancel();
    }

    @Inject(at = {@At("HEAD")},
            method = {
                    "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"})
    private void onMove(MovementType type, Vec3d offset, CallbackInfo callbackInfo)
    {
        OnMoveEvent event = new OnMoveEvent(this);
        KiwiClient.eventBus.post(event);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(String message, CallbackInfo callbackInfo) {
        if (ignoreChatMessage) return;

        if (!message.startsWith(KiwiClient.PREFIX) && !message.startsWith("/")) {
            SendChatMessageEvent event = new SendChatMessageEvent(message);
            KiwiClient.eventBus.post(event);

            if (!event.isCancelled()) {
                ignoreChatMessage = true;
                sendChatMessage(event.message);
                ignoreChatMessage = false;
            }

            callbackInfo.cancel();
            return;
        }

        if (message.startsWith(KiwiClient.PREFIX)) {
            try {
                KiwiClient.commandManager.dispatch(message.substring(KiwiClient.PREFIX.length()));
            } catch (CommandSyntaxException e) {
                Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText(e.getMessage()));
            }
            callbackInfo.cancel();
        }
    }

    @Override
    public void setNoClip(boolean noClip)
    {
        this.noClip = noClip;
    }

    @Override
    protected boolean clipAtLedge() {
        return super.clipAtLedge() || KiwiClient.moduleManager.getModule(SafeWalk.class).isEnabled();
    }

    @Redirect(method = "updateNausea()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;closeHandledScreen()V", ordinal = 0))
    private void updateNausea_closeHandledScreen(ClientPlayerEntity player) {
        if (!KiwiClient.moduleManager.getModule(NoPortal.class).isEnabled()) closeHandledScreen();
    }

    @Redirect(method = "updateNausea()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", ordinal = 0))
    private void updateNausea_openScreen(MinecraftClient player, Screen screen_1) {
        if (!KiwiClient.moduleManager.getModule(NoPortal.class).isEnabled()) client.setScreen(screen_1);
    }
}
