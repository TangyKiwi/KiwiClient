package com.tangykiwi.kiwiclient.modules.movement;

import com.tangykiwi.kiwiclient.event.IsFullCubeEvent;
import com.tangykiwi.kiwiclient.event.MarkClosedEvent;
import com.tangykiwi.kiwiclient.event.OnMoveEvent;
import com.tangykiwi.kiwiclient.mixininterface.IClientPlayerEntity;
import com.tangykiwi.kiwiclient.modules.Category;
import net.minecraft.client.network.ClientPlayerEntity;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Module;
import org.lwjgl.glfw.GLFW;

public class NoClip extends Module {

    public NoClip() {
        super("NoClip", "Clip through blocks", GLFW.GLFW_KEY_N, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        ClientPlayerEntity player = mc.player;

        player.noClip = true;
        player.fallDistance = 0;
        player.setOnGround(false);

        player.abilities.flying = false;
        player.setVelocity(0, 0, 0);

        float speed = 0.2F;
        player.flyingSpeed = speed;

        if(mc.options.keyJump.isPressed())
            player.addVelocity(0, speed, 0);
        if(mc.options.keySneak.isPressed())
            player.addVelocity(0, -speed, 0);
    }

    @Subscribe
    public void onPlayerMove(OnMoveEvent event)
    {
        event.getPlayer().setNoClip(true);
    }

    @Subscribe
    public void onIsFullCube(IsFullCubeEvent event)
    {
        event.setCancelled(true);
    }

    @Subscribe
    public void onMarkClosed(MarkClosedEvent event)
    {
        event.setCancelled(true);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.player.noClip = false;
    }
}
