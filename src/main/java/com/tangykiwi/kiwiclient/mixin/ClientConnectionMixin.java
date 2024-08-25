package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.ReceivePacketEvent;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/packet/Packet;)V", cancellable = true)
    private void onSendPacketHead(Packet<?> packet, CallbackInfo info) {
        SendPacketEvent event = new SendPacketEvent(packet, (ClientConnection)(Object)this);
        KiwiClient.eventBus.post(event);
        
        if(event.isCancelled()) info.cancel();
    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void onHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo info) {
        ReceivePacketEvent event = new ReceivePacketEvent(packet);
        KiwiClient.eventBus.post(event);

        if (event.isCancelled()) info.cancel();
    }
}
