package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.SendPacketEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.mixin.PlayerMoveC2SPacketAccessor;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

public class NoFall extends Module {

    public NoFall() {
        super("NoFall", "Prevents fall damage", KEY_UNBOUND, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent e) {
//        if (mc.player.fallDistance > 2.5f /*&& getSetting(0).asMode().mode == 0*/) {
//            if (mc.player.isFallFlying())
//                return;
//            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
//        }

//        if (mc.player.fallDistance > 2.5f && && getSetting(0).asMode().mode == 1 &&
//                mc.world.getBlockState(mc.player.getBlockPos().add(
//                        0, -1.5 + (mc.player.getVelocity().y * 0.1), 0)).getBlock() != Blocks.AIR) {
//            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
//            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
//                    mc.player.getX(), mc.player.getY() - 420.69, mc.player.getZ(), true));
//            mc.player.fallDistance = 0;
//        }
    }

    @Subscribe
    public void onSendPacket(SendPacketEvent event) {
//        if (mc.player.getAbilities().creativeMode
//                || !(event.packet instanceof PlayerMoveC2SPacket)
//                || mode.get() != Mode.Packet
//                || ((IPlayerMoveC2SPacket) event.packet).getTag() == 1337) return;


        if ((mc.player.isFallFlying() || KiwiClient.moduleManager.getModule(Fly.class).isEnabled()) && mc.player.getVelocity().y < 1) {
            BlockHitResult result = mc.world.raycast(new RaycastContext(
                    mc.player.getPos(),
                    mc.player.getPos().subtract(0, 0.5, 0),
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    mc.player)
            );

            if (result != null && result.getType() == HitResult.Type.BLOCK) {
                ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
            }
        }
        else {
            ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
        }
    }
}
