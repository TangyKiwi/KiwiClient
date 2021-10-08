package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.EntityRenderEvent;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.EntityUtils;
import com.tangykiwi.kiwiclient.util.font.IFont;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Nametags extends Module {
    public Nametags() {
        super("Nametags", "Shows nametags above entities", KEY_UNBOUND, Category.RENDER);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onLivingLabelRender(EntityRenderEvent.Single.Label event) {
        if(EntityUtils.isAnimal(event.getEntity()) ||
            event.getEntity() instanceof Monster ||
            event.getEntity() instanceof PlayerEntity ||
            event.getEntity() instanceof ItemEntity) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onWorldRender(WorldRenderEvent.Post event) {
        for (Entity entity : mc.world.getEntities()) {
            Vec3d rPos = entity.getPos().subtract(RenderUtils.getInterpolationOffset(entity)).add(0, entity.getHeight(), 0);

            double d = entity.squaredDistanceTo(mc.cameraEntity);
            if (!(d > 4096.0D)) {
                float scale = 1f;

                if (Math.sqrt(d) > 10 ) scale *= Math.sqrt(d) / 10;

                if (entity instanceof LivingEntity) {
                    if (entity == mc.player || entity.hasPassenger(mc.player) || mc.player.hasPassenger(entity)) {
                        continue;
                    }

                    LivingEntity livingEntity = (LivingEntity) entity;
                    String name = livingEntity.getName().getString();
                    String health = String.format("%.1f", livingEntity.getHealth());

                    RenderUtils.drawWorldText(name + " " + getEmptyString(health), rPos.x, rPos.y + 0.5, rPos.z, scale, 0xFFFFFF);
                    RenderUtils.drawWorldText(getEmptyString(name) + " " + health, rPos.x, rPos.y + 0.5, rPos.z, scale, getHealthColor(livingEntity));
                }
            }
        }
    }

    public String getEmptyString(String string) {
        String empty = "";
        while (IFont.CONSOLAS.getStringWidth(empty) < IFont.CONSOLAS.getStringWidth(string)) {
            empty += " ";
        }
        return empty;
    }

    public int getHealthColor(LivingEntity entity) {
        float health = entity.getHealth() + entity.getAbsorptionAmount();
        float maxHealth = entity.getMaxHealth();

        if (health > maxHealth) {
            return 0xFFFF55;
        }
        else if (health >= maxHealth * 0.7) {
            return 0x55FF55;
        }
        else if (health >= maxHealth * 0.4) {
            return 0xFFAA00;
        }
        else if (health >= maxHealth * 0.1) {
            return 0xFF5555;
        }
        return 0xAA0000;
    }
}
