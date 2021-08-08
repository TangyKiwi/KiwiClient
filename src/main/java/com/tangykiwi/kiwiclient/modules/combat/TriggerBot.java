package com.tangykiwi.kiwiclient.modules.combat;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
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
        if(KiwiClient.moduleManager.getModule(Criticals.class).isEnabled()) ((Criticals) KiwiClient.moduleManager.getModule(Criticals.class)).doCritical();
        mc.interactionManager.attackEntity(player, target);
        player.swingHand(Hand.MAIN_HAND);
    }
}
