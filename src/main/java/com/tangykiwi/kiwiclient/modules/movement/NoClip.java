package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.IsFullCubeEvent;
import com.tangykiwi.kiwiclient.event.MarkClosedEvent;
import com.tangykiwi.kiwiclient.event.OnMoveEvent;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.client.network.ClientPlayerEntity;

public class NoClip extends Module {

    public NoClip() {
        super("NoClip", "Clip through blocks", KEY_UNBOUND, Category.MOVEMENT);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent e) {
        ClientPlayerEntity player = mc.player;

        player.noClip = true;
        player.fallDistance = 0;
        player.setOnGround(false);

        player.getAbilities().flying = false;
        player.setVelocity(0, 0, 0);

        float speed = 0.2F;
        player.flyingSpeed = speed;

        if(mc.options.keyJump.isPressed())
            player.addVelocity(0, speed, 0);
        if(mc.options.keySneak.isPressed())
            player.addVelocity(0, -speed, 0);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onPlayerMove(OnMoveEvent event)
    {
        event.getPlayer().setNoClip(true);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onIsFullCube(IsFullCubeEvent event)
    {
        event.setCancelled(true);
    }

    @Subscribe
    @AllowConcurrentEvents
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
