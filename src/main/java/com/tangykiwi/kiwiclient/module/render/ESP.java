package com.tangykiwi.kiwiclient.module.render;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ModeSetting;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.util.EntityUtils;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;

public class ESP extends Module {
    public ESP() {
        super("ESP", "Highlights entities", Category.RENDER,
                new ModeSetting("Mode", "ESP mode", "Shader", "Box+Fill", "Box", "Fill"),
                new SliderSetting("Box", "Box line thickness", 0.1, 4, 2, 1),
                new SliderSetting("Fill", "Fill opacity", 0, 1, 0.3, 2));
    }

    // shader handling done in MinecraftClientMixin, WorldRendererMixin

    @Subscribe
    public void onWorldRenderPost(WorldRenderEvent.Post event) {
        if (getSetting("Mode").asMode().getValue() != 0) {
            double width = getSetting("Box").asSlider().getValueD();
            float fill = getSetting("Fill").asSlider().getValueFloat();

            for (Entity entity : mc.world.getEntities()) {
                if (entity == mc.player || entity == mc.player.getVehicle()) continue;
                int[] color_arr = getColor(entity);
                if (color_arr != null) {
                    if (fill != 0 && (getSetting("Mode").asMode().getValue() == 1 || getSetting("Mode").asMode().getValue() == 3)) {
                        int color = ((int)(fill * 255) << 24) | (color_arr[0] << 16) | (color_arr[1] << 8) | color_arr[2];
                        RenderUtils.drawBoxFilled(entity.getBoundingBox(), color);
                    }
                    if (getSetting("Mode").asMode().getValue() == 1 || getSetting("Mode").asMode().getValue() == 2) {
                        int color = (255 << 24) | (color_arr[0] << 16) | (color_arr[1] << 8) | color_arr[2];
                        RenderUtils.drawBoxOutline(entity.getBoundingBox(), color, width);
                    }

                }
            }
        }
    }

    public int[] getColor(Entity entity) {
        if (EntityUtils.isPlayer(entity)) {
            return new int[] { 255, 255, 255 };
        } else if (EntityUtils.isMob(entity)) {
            return new int[] { 255, 0, 0 };
        } else if (EntityUtils.isAnimal(entity)) {
            return new int[] { 77, 255, 77 };
        } else if (entity instanceof ItemEntity || entity instanceof EndCrystalEntity || entity instanceof BoatEntity || entity instanceof AbstractMinecartEntity || entity instanceof ItemFrameEntity) {
            return new int[] { 128, 128, 128 };
        }

        return null;
    }
}
