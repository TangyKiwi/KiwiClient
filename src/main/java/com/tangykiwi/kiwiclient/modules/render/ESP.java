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
import com.tangykiwi.kiwiclient.util.render.shader.OutlineShaderManager;
import com.tangykiwi.kiwiclient.util.render.shader.ShaderEffectLoader;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class ESP extends Module {
    private int lastWidth = -1;
    private int lastHeight = -1;
    private double lastShaderWidth;
    private boolean shaderUnloaded = true;

    public ESP() {
        super("ESP", "Highlights entities", KEY_UNBOUND, Category.RENDER,
            new ModeSetting("Mode", "Shader", "Box+Fill", "Box", "Fill").withDesc("ESP Mode"),
            new SliderSetting("Shader", 0, 6, 2, 0).withDesc("Shader outline thickness"),
            new SliderSetting("Box", 0.1, 4, 2, 1).withDesc("Box line thickness"),
            new SliderSetting("Fill", 0, 1, 0.3, 2).withDesc("Fill opacity"));
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onEntityRenderPre(EntityRenderEvent.PreAll event) {
        if (getSetting(0).asMode().mode <= 1) {
            if (mc.getWindow().getFramebufferWidth() != lastWidth || mc.getWindow().getFramebufferHeight() != lastHeight
                    || lastShaderWidth != getSetting(1).asSlider().getValue() || shaderUnloaded) {
                try {
                    ShaderEffect shader = ShaderEffectLoader.load(mc.getFramebuffer(), "esp-shader",
                            String.format(
                                    Locale.ENGLISH,
                                    IOUtils.toString(getClass().getResource("/assets/kiwiclient/shaders/mc_outline.ujson"), StandardCharsets.UTF_8),
                                    getSetting(1).asSlider().getValue() / 2,
                                    getSetting(1).asSlider().getValue() / 4));

                    shader.setupDimensions(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
                    lastWidth = mc.getWindow().getFramebufferWidth();
                    lastHeight = mc.getWindow().getFramebufferHeight();
                    lastShaderWidth = getSetting(1).asSlider().getValue();
                    shaderUnloaded = false;

                    OutlineShaderManager.loadShader(shader);
                } catch (JsonSyntaxException | IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (!shaderUnloaded) {
            OutlineShaderManager.loadDefaultShader();
            shaderUnloaded = true;
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onWorldRenderPost(WorldRenderEvent.Post event) {
        for (Entity e: mc.world.getEntities()) {
            if (e == mc.player || e == mc.player.getVehicle()) {
                continue;
            }

            float[] color = getColorForEntity(e);
            if (color != null) {

                if (getSetting(0).asMode().mode == 1 || getSetting(0).asMode().mode == 3) {
                    RenderUtils.drawBoxFill(e.getBoundingBox(), QuadColor.single(color[0], color[1], color[2], getSetting(3).asSlider().getValueFloat()));
                }

                if (getSetting(0).asMode().mode == 1 || getSetting(0).asMode().mode == 2) {
                    RenderUtils.drawBoxOutline(e.getBoundingBox(), QuadColor.single(color[0], color[1], color[2], 1f), getSetting(2).asSlider().getValueFloat());
                }
            }
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onEntityRender(EntityRenderEvent.Single.Pre event) {
        float[] color = getColorForEntity(event.getEntity());

        if (color != null && getSetting(0).asMode().mode == 0) {
            event.setVertex(getOutline(mc.getBufferBuilders(), color[0], color[1], color[2]));
        }
    }

    private float[] getColorForEntity(Entity entity) {
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

    private VertexConsumerProvider getOutline(BufferBuilderStorage buffers, float r, float g, float b) {
        OutlineVertexConsumerProvider ovsp = buffers.getOutlineVertexConsumers();
        ovsp.setColor((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
        return ovsp;
    }
}
