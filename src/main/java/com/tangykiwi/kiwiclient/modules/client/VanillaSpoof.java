package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

public class VanillaSpoof extends Module {
    public VanillaSpoof() {
        super("VanillaSpoof", "Spoofs your client type to the server", KEY_UNBOUND, Category.CLIENT,
            new ModeSetting("Type","Vanilla", "KiwiClient"));
    }

    @Subscribe
    public void onSendPacket(SendPacketEvent event) {
        if(event.packet instanceof CustomPayloadC2SPacket packet) {
            Identifier id = packet.payload().getId().id();
            if(id.equals(BrandCustomPayload.ID.id())) {
                ModeSetting setting = getSetting(0).asMode();
                CustomPayloadC2SPacket spoofedPacket = new CustomPayloadC2SPacket(new BrandCustomPayload(setting.modes[setting.mode].toLowerCase()));
                event.connection.send(spoofedPacket);
            }
        }
    }
}
