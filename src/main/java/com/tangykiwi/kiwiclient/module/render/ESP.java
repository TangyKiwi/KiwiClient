package com.tangykiwi.kiwiclient.module.render;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.LevelRenderEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ModeSetting;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.util.EntityUtils;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

public class ESP extends Module {
    public ESP() {
        super("ESP", "Highlights entities", Category.RENDER,
                new ModeSetting("Mode", "ESP mode", "Shader", "Box+Fill", "Box", "Fill"),
                new SliderSetting("Box", "Box line thickness", 0.1, 4, 2, 1),
                new SliderSetting("Fill", "Fill opacity", 0, 1, 0.3, 2));
    }

    // shader handling done in MinecraftClientMixin, WorldRendererMixin

    @Subscribe
    public void onWorldRenderPost(LevelRenderEvent event) {
        if (getSetting("Mode").asMode().getValue() != 0) {
            float width = getSetting("Box").asSlider().getValueFloat();
            float fill = getSetting("Fill").asSlider().getValueFloat();

            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity == mc.player || entity == mc.player.getVehicle()) continue;
                int color = getColor(entity);
                if (color != -1) {
                    if (fill != 0 && (getSetting("Mode").asMode().getValue() == 1 || getSetting("Mode").asMode().getValue() == 3)) {
                        int fillColor = ((int)(fill * 255) << 24) | color;
                        RenderUtils.drawBoxFilled(entity.getBoundingBox(), fillColor);
                    }
                    if (getSetting("Mode").asMode().getValue() == 1 || getSetting("Mode").asMode().getValue() == 2) {
                        int outlineColor = (255 << 24) | color;
                        RenderUtils.drawBoxOutline(entity.getBoundingBox(), outlineColor, width);
                    }

                }
            }
        }
    }

    public int getColor(Entity entity) {
        if (EntityUtils.isPlayer(entity)) {
            return (255 << 16) | (255 << 8) | 255;
        } else if (EntityUtils.isMob(entity)) {
            return (255 << 16) | (0 << 8) | 0;
        } else if (EntityUtils.isAnimal(entity)) {
            return (77 << 16) | (255 << 8) | 77;
        }
        return (128 << 16) | (128 << 8) | 128;
    }
}
