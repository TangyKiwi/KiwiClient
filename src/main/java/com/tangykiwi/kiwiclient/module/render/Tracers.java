package com.tangykiwi.kiwiclient.module.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.LevelRenderEvent;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.SliderSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;
import com.tangykiwi.kiwiclient.util.EntityUtils;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class Tracers extends Module {
    public Tracers() {
        super("Tracers", "Draws lines to entities", Category.RENDER,
            new SliderSetting("Width", "Tracer line width", 0.1, 5, 1.5, 1),
            new SliderSetting("Opacity", "Tracer line opacity", 0, 1, 0.75, 2),
            new ToggleSetting("Players", "Draw tracers to players", true),
            new ToggleSetting("Animals", "Draw tracers to animals", false),
            new ToggleSetting("Mobs", "Draw tracers to hostile mobs", false));
    }

    @Subscribe
    public void onRender(LevelRenderEvent event) {
        float width = getSetting(0).asSlider().getValueFloat();
        float opacity = getSetting(1).asSlider().getValueFloat();

        for(Entity e : mc.level.entitiesForRendering()) {
            Vec3 vec = e.getEyePosition().subtract(RenderUtils.getInterpolationOffset(e));

            Vec3 vec2 = new Vec3(0, 0, 75)
                        .xRot(-(float) Math.toRadians(mc.gameRenderer.mainCamera().xRot()))
                        .yRot(-(float) Math.toRadians(mc.gameRenderer.mainCamera().yRot()))
                        .add(mc.getCameraEntity().getEyePosition());

            int color = -1;

            if(EntityUtils.isPlayer(e) && e != mc.player && e != mc.getCameraEntity() && getSetting("Players").asToggle().getValue()) {
                color = getColor(e);
            } else if(EntityUtils.isAnimal(e) && getSetting("Animals").asToggle().getValue()) {
                color = getColor(e);
            } else if(EntityUtils.isMob(e) && getSetting("Mobs").asToggle().getValue()) {
                color = getColor(e);
            }

            if (color != -1) {
                color = ((int)(opacity * 255) << 24) | (color & 0x00FFFFFF);
                RenderUtils.drawLine(vec2.x, vec2.y, vec2.z, vec.x, vec.y, vec.z, color, width);
            }
        }
    }

    private int getColor(Entity e) {
        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();

        double ex = e.getX();
        double ey = e.getY();
        double ez = e.getZ();

        double dist = Math.sqrt(Math.pow(px - ex, 2) + Math.pow(py - ey, 2) + Math.pow(pz - ez, 2));

        double ratio = dist / 80;
        if(ratio > 1) ratio = 1;

        return transitionOfHueRange(ratio, 0, 120);
    }

    public int transitionOfHueRange(double percentage, int startHue, int endHue) {
        double hue = ((percentage * (endHue - startHue)) + startHue) / 360;

        double saturation = 1.0;
        double lightness = 0.5;

        return hslColorToRgb(hue, saturation, lightness);
    }

    public int hslColorToRgb(double hue, double saturation, double lightness) {
        if (saturation == 0.0) {
            int grey = percToColor(lightness);
            return (grey << 16) | (grey << 8) | grey;
        }

        double q;
        if (lightness < 0.5) {
            q = lightness * (1 + saturation);
        } else {
            q = lightness + saturation - lightness * saturation;
        }
        double p = 2 * lightness - q;

        double oneThird = 1.0 / 3;
        int red = percToColor(hueToRgb(p, q, hue + oneThird));
        int green = percToColor(hueToRgb(p, q, hue));
        int blue = percToColor(hueToRgb(p, q, hue - oneThird));

        return (red << 16) | (green << 8) | blue;
    }

    public double hueToRgb(double p, double q, double t) {
        if (t < 0) {
            t += 1;
        }
        if (t > 1) {
            t -= 1;
        }

        if (t < 1.0 / 6) {
            return p + (q - p) * 6 * t;
        }
        if (t < 1.0 / 2) {
            return q;
        }
        if (t < 2.0 / 3) {
            return p + (q - p) * (2.0 / 3 - t) * 6;
        }
        return p;
    }

    public int percToColor(double percentage) {
        return (int) (percentage * 255);
    }
}
