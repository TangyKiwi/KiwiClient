package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", "Prevents fall damage", KEY_UNBOUND, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        ClientPlayerEntity player = mc.player;
        if(mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) return;

        if(player.fallDistance <= (player.isFallFlying() ? 1 : 2)) return;

        if(player.isFallFlying() && player.isSneaking() && player.getVelocity().y >= -0.5) return;

        player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
    }
}
