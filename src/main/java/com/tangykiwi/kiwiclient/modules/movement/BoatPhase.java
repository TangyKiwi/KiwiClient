package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.text.Text;

public class BoatPhase extends Module {
    public BoatPhase() {
        super("Boat Phase", "Phase through walls with a boat", KEY_UNBOUND, Category.MOVEMENT);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onTick(TickEvent event) {
        if(!(mc.player.getVehicle() instanceof BoatEntity)) {
            mc.inGameHud.getChatHud().addMessage(Text.literal("You need a boat"));
            setToggled(false);
            return;
        }

        mc.player.noClip = true;
        mc.player.getVehicle().noClip = true;
        mc.player.getVehicle().setNoGravity(true);
    }
}
