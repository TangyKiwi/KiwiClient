package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonSyntaxException;
import com.tangykiwi.kiwiclient.event.EntityRenderEvent;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.util.EntityUtils;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import com.tangykiwi.kiwiclient.util.render.color.QuadColor;
import com.tangykiwi.kiwiclient.util.render.shader.ColorVertexConsumerProvider;
import com.tangykiwi.kiwiclient.util.render.shader.ShaderCore;
import com.tangykiwi.kiwiclient.util.render.shader.ShaderEffectWrapper;
import com.tangykiwi.kiwiclient.util.render.shader.ShaderLoader;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;

public class ESP extends Module {
    private ShaderEffectWrapper shader;
    private ColorVertexConsumerProvider colorVertexer;

    public ESP() {
        super("ESP", "Highlights entities", GLFW.GLFW_KEY_R, Category.RENDER,
            new ModeSetting("Mode", "Shader", "Box+Fill", "Box", "Fill").withDesc("ESP Mode"),
            new SliderSetting("Shader", 0, 6, 2, 0).withDesc("Shader outline thickness"),
            new SliderSetting("Box", 0.1, 4, 2, 1).withDesc("Box line thickness"),
            new SliderSetting("Fill", 0, 1, 0.3, 2).withDesc("Fill opacity"));
    }

    @Override
    public void onEnable() {
        super.onEnable();

        try {
            shader = new ShaderEffectWrapper(
                    ShaderLoader.loadEffect(mc.getFramebuffer(), new Identifier("kiwiclient", "shaders/post/entity_outline.json")));
            colorVertexer = new ColorVertexConsumerProvider(shader.getFramebuffer("main"), ShaderCore::getColorOverlayShader);
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
            super.onDisable();
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onWorldRender(WorldRenderEvent.Pre event) {
        shader.prepare();
        shader.clearFramebuffer("main");
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onEntityRender(EntityRenderEvent.Single.Pre event) {
        if (getSetting(0).asMode().mode != 0 || event.getEntity() == null)
            return;

        float[] color = getColor(event.getEntity());
        color[0] = (int) (color[0] * 255);
        color[1] = (int) (color[1] * 255);
        color[2] = (int) (color[2] * 255);

        if (color != null) {
            event.setVertex(colorVertexer.createDualProvider(event.getVertex(), (int) color[0], (int) color[1], (int) color[2], getSetting(1).asSlider().getValueInt()));
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onWorldRender(WorldRenderEvent.Post event) {
        if (getSetting(0).asMode().mode == 0) {
            colorVertexer.draw();
            shader.render();
            shader.drawFramebufferToMain("main");
        } else {
            float width = getSetting(2).asSlider().getValueFloat();
            float fill = getSetting(3).asSlider().getValueFloat();

            for (Entity e: mc.world.getEntities()) {

                if (e == mc.player || e == mc.player.getVehicle()) {
                    continue;
                }

                float[] color = getColor(e);

                if (color != null) {
                    if (width != 0 && (getSetting(0).asMode().mode == 1 || getSetting(0).asMode().mode == 2)) {
                        RenderUtils.drawBoxOutline(e.getBoundingBox(), QuadColor.single(color[0], color[1], color[2], 1f), width);
                    }

                    if (fill != 0 && (getSetting(0).asMode().mode == 1 || getSetting(0).asMode().mode == 3)) {
                        RenderUtils.drawBoxFill(e.getBoundingBox(), QuadColor.single(color[0], color[1], color[2], fill));
                    }
                }
            }
        }
    }

    private float[] getColor(Entity entity) {
        if (EntityUtils.isPlayer(entity)) {
            return new float[] { 1.0F, 1.0F, 1.0F };
        } else if (EntityUtils.isMob(entity)) {
            return new float[] { 1.0F, 0.0F, 0.0F };
        } else if (EntityUtils.isAnimal(entity)) {
            return new float[] { 0.3F, 1.0F, 0.3F };
        } else if (entity instanceof ItemEntity || entity instanceof EndCrystalEntity || entity instanceof BoatEntity || entity instanceof AbstractMinecartEntity || entity instanceof ItemFrameEntity) {
            return new float[] { 0.5F, 0.5F, 0.5F };
        }

        return null;
    }
}
