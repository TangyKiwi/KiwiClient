package com.tangykiwi.kiwiclient.modules.player;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.Deadmau5EarsRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;

public class Deadmau5Ears extends Module {
    public Deadmau5Ears() {
        super("Deadmau5Ears", "Gives you ears like Deadmau5", KEY_UNBOUND, Category.PLAYER);
    }
}
