package com.tangykiwi.kiwiclient.modules.combat;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class TriggerBot extends Module {

    public TriggerBot() {
        super("TriggerBot", "Attacks the entity you are looking at.", GLFW.GLFW_KEY_X, Category.COMBAT);
    }


    @Subscribe
    public void onTick(TickEvent event)
    {
        ClientPlayerEntity player = mc.player;
        if(player.getAttackCooldownProgress(0) < 1)
            return;

        if(mc.crosshairTarget == null || !(mc.crosshairTarget instanceof EntityHitResult)) return;

        Entity target = ((EntityHitResult) mc.crosshairTarget).getEntity();
        if(KiwiClient.moduleManager.getModule(Criticals.class).isEnabled()) this.doCritical();
        mc.interactionManager.attackEntity(player, target);
        player.swingHand(Hand.MAIN_HAND);
    }

    public void doCritical() {
        if (mc.player.isInLava() || mc.player.isTouchingWater()) return;
        double posX = mc.player.getX();
        double posY = mc.player.getY();
        double posZ = mc.player.getZ();
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(posX, posY + 0.0625, posZ, true));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(posX, posY, posZ, false));
    }
}
