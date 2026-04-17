package com.tangykiwi.kiwiclient.module.client;

import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.resources.Identifier;

import org.apache.commons.lang3.Strings;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import com.tangykiwi.kiwiclient.module.Category;

public class VanillaSpoof extends Module {
    public VanillaSpoof() {
        super("VanillaSpoof", "Spoofs the client brand name.", Category.CLIENT);
    }

    @Subscribe
    public void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof ServerboundCustomPayloadPacket) {
            Identifier id = ((ServerboundCustomPayloadPacket) event.packet).payload().type().id();

            String[] channels = {"fabric", "minecraft:register"};
            for (String channel : channels) {
                if (Strings.CI.contains(id.toString(), channel)) {
                    event.cancel();
                    return;
                }
            }

            if (id.equals(BrandPayload.TYPE.id())) {
                ServerboundCustomPayloadPacket spoofedPacket = new ServerboundCustomPayloadPacket(new BrandPayload("vanilla"));

                event.connection.send(spoofedPacket, null, true);
                event.cancel();
            }
        }
    }
}
