package com.tangykiwi.kiwiclient.mixin;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.combat.BowBomb;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(
            at = {@At("HEAD")},
            method = {"stopUsingItem"}
    )
    private void onStopUsingItem(PlayerEntity player, CallbackInfo ci) {
        if (KiwiClient.moduleManager.getModule(BowBomb.class).isEnabled() && player.getInventory().getMainHandStack().getItem().equals(Items.BOW)) {

            Utils.mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(Utils.mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));

            for(int i = 0; i < 100; i++) {
                Utils.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Utils.mc.player.getX(), Utils.mc.player.getY() - 0.000000001, Utils.mc.player.getZ(), true));
                Utils.mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Utils.mc.player.getX(), Utils.mc.player.getY() + 0.000000001, Utils.mc.player.getZ(), false));
            }

        }
    }
}
