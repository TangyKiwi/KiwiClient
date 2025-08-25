package com.tangykiwi.kiwiclient.module.combat;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.lwjgl.glfw.GLFW;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class TriggerBot extends Module {
    public TriggerBot() {
        super("TriggerBot", "Automatically attacks the entity you are looking at", GLFW.GLFW_KEY_X, Category.COMBAT);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        ClientPlayerEntity player = mc.player;

        if(player == null || !player.isAlive() || player.isSpectator() || player.getAttackCooldownProgress(0) < 1 || mc.currentScreen instanceof HandledScreen) return;

        Entity target = mc.targetedEntity;
        if (target == null || (target instanceof LivingEntity && ((LivingEntity) target).isDead()) || !target.isAlive()) return;

        mc.interactionManager.attackEntity(player, target);
        player.swingHand(Hand.MAIN_HAND);
    }
}
