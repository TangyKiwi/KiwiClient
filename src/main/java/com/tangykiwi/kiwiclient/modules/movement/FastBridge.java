package com.tangykiwi.kiwiclient.modules.movement;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.TickEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class FastBridge extends Module {

    public FastBridge() {
        super("FastBridge", "Autosneaks at block edge.", GLFW.GLFW_KEY_G, Category.MOVEMENT);
    }

    @Subscribe
    public void onTick(TickEvent e) {
        Vec3d pos = mc.player.getPos();
        mc.options.keySneak.setPressed(mc.world.getBlockState(new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ())).isAir());
    }

    @Override
    public void onDisable() {
        mc.options.keySneak.setPressed(false);
        super.onDisable();
    }
}
