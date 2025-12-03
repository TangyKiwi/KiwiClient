package com.tangykiwi.kiwiclient.module.client;

import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.network.packet.BrandCustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import org.apache.commons.lang3.StringUtils;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import com.tangykiwi.kiwiclient.module.Category;

public class VanillaSpoof extends Module {
    public VanillaSpoof() {
        super("VanillaSpoof", "Spoofs the client brand name.", Category.CLIENT);
    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof CustomPayloadC2SPacket) {
            Identifier id = ((CustomPayloadC2SPacket) event.packet).payload().getId().id();

            String[] channels = {"fabric", "minecraft:register"};
            for (String channel : channels) {
                if (StringUtils.containsIgnoreCase(id.toString(), channel)) {
                    event.cancel();
                    return;
                }
            }

            if (id.equals(BrandCustomPayload.ID.id())) {
                CustomPayloadC2SPacket spoofedPacket = new CustomPayloadC2SPacket(new BrandCustomPayload("vanilla"));

                event.connection.send(spoofedPacket, null, true);
                event.cancel();
            }
        }
    }
}
