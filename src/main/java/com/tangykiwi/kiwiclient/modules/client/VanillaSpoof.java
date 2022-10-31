package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import com.tangykiwi.kiwiclient.mixin.CustomPayloadC2SPacketAccessor;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

import java.nio.charset.StandardCharsets;

public class VanillaSpoof extends Module {
    public VanillaSpoof() {
        super("VanillaSpoof", "Spoofs your client type to the server", KEY_UNBOUND, Category.CLIENT,
            new ModeSetting("Type","Vanilla", "KiwiClient"));
    }

    @Subscribe
    public void onSendPacket(SendPacketEvent event) {
        if(event.packet instanceof CustomPayloadC2SPacket packet) {
            CustomPayloadC2SPacketAccessor accessor = (CustomPayloadC2SPacketAccessor) packet;
            ModeSetting setting = getSetting(0).asMode();
            if(accessor.getChannel().equals(CustomPayloadC2SPacket.BRAND)) {
                accessor.setData(new PacketByteBuf(Unpooled.buffer()).writeString(setting.modes[setting.mode].toLowerCase()));
            } else if (accessor.getData().toString(StandardCharsets.UTF_8).toLowerCase().contains("fabric")) {
                event.setCancelled(true);
            }
        }
    }
}
