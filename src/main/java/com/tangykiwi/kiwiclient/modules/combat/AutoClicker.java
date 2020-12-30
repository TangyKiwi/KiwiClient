package com.tangykiwi.kiwiclient.modules.combat;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

public class AutoClicker extends Module {
    public AutoClicker() {
        super("Autoclicker", "Clicks really fast", GLFW.GLFW_KEY_J, Category.COMBAT);
    }


    @Subscribe
    public void onTick(TickEvent event)
    {
        mc.options.keyAttack.setPressed(true);
    }

}
