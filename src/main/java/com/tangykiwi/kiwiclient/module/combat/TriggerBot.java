package com.tangykiwi.kiwiclient.module.combat;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.lwjgl.glfw.GLFW;

import java.util.Random;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class TriggerBot extends Module {
    public TriggerBot() {
        super("TriggerBot", "Automatically attacks the entity you are looking at", GLFW.GLFW_KEY_X, Category.COMBAT,
            new ToggleSetting("Delay", "Enable random delay", false).withChildren(
                new SliderSetting("Time", "Average random delay in milliseconds", 0, 500, 100, -1)
            )
        );
    }

    private long lastAttack = 0;
    private long randomDelay = 0;
    private final Random random = new Random();

    @Subscribe
    public void onTick(TickEvent.Pre e) {
        ToggleSetting delay = getSetting(0).asToggle();
        if (delay.getValue()) {
            if (System.currentTimeMillis() - lastAttack < randomDelay) {
                return;
            }
        }

        Player player = mc.player;

        if(player == null || !player.isAlive() || player.isSpectator() || player.getAttackStrengthScale(1) < 1 || mc.gui.screen() instanceof AbstractContainerScreen) return;

        Entity target = mc.crosshairPickEntity;
        if (target == null || (target instanceof LivingEntity && ((LivingEntity) target).isDeadOrDying()) || !target.isAlive()) return;

        Criticals criticals = (Criticals) KiwiClient.moduleManager.getModule(Criticals.class);
        if (criticals.isEnabled()) {
            criticals.doCritical(target);
        }
        mc.gameMode.attack(player, target);
        
        if (delay.getValue()) {
            lastAttack = System.currentTimeMillis();
            randomDelay = (long) (delay.getChild(0).asSlider().getValueD() * (1 + random.nextGaussian() * 0.18));
        }
        player.swing(InteractionHand.MAIN_HAND);
    }
}
