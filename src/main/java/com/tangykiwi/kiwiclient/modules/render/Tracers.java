package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.WorldRenderEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.SliderSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import com.tangykiwi.kiwiclient.util.EntityUtils;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;
import com.tangykiwi.kiwiclient.util.render.color.LineColor;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class Tracers extends Module {

    public Tracers() {
        super("Tracers", "Draws lines to entities", KEY_UNBOUND, Category.RENDER,
            new SliderSetting("Width", 0.1, 5, 1.5, 1).withDesc("Tracer line width"),
            new SliderSetting("Opacity", 0, 1, 0.75, 2).withDesc("Tracer line opacity"),
            new ToggleSetting("Players", true).withDesc("Draw tracers to players"),
            new ToggleSetting("Animals", false).withDesc("Draw tracers to animals"),
            new ToggleSetting("Mobs", false).withDesc("Draw tracers to hostile mobs"));
    }

    @Subscribe
    public void onRender(WorldRenderEvent.Post event) {
        float width = getSetting(0).asSlider().getValueFloat();
        float opacity = getSetting(1).asSlider().getValueFloat();

        for(Entity e : mc.world.getEntities()) {
            Vec3d vec = e.getPos().subtract(RenderUtils.getInterpolationOffset(e));

            Vec3d vec2 = new Vec3d(0, 0, 75)
                .rotateX(-(float) Math.toRadians(mc.gameRenderer.getCamera().getPitch()))
                .rotateY(-(float) Math.toRadians(mc.gameRenderer.getCamera().getYaw()))
                .add(mc.cameraEntity.getEyePos());

            int[] color = null;

            if(EntityUtils.isPlayer(e) && e != mc.player && e != mc.cameraEntity && getSetting(2).asToggle().state) {
                color = getColor(e);
            } else if(EntityUtils.isAnimal(e) && getSetting(3).asToggle().state) {
                color = getColor(e);
            } else if(EntityUtils.isMob(e) && getSetting(4).asToggle().state) {
                color = getColor(e);
            }

            if (color != null) {
                RenderUtils.drawLine(vec2.x, vec2.y, vec2.z, vec.x, vec.y + e.getHeight() / 2, vec.z, LineColor.single(color[0], color[1], color[2], (int) (opacity * 255)), width);
                //RenderUtils.drawLine(vec.x, vec.y, vec.z, vec.x, vec.y + e.getHeight() * 0.9, vec.z, LineColor.single(color[0], color[1], color[2], opacity), width);
            }
        }
    }

    private int[] getColor(Entity e) {
        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();

        double ex = e.getX();
        double ey = e.getY();
        double ez = e.getZ();

        double dist = Math.sqrt(Math.pow(px - ex, 2) + Math.pow(py - ey, 2) + Math.pow(pz - ez, 2));

        double ratio = dist / 80;
        if(ratio > 1) ratio = 1;

        Color color = transitionOfHueRange(ratio, 0, 120);

        return new int[] { color.getRed(), color.getGreen(), color.getBlue() };
    }

    public Color transitionOfHueRange(double percentage, int startHue, int endHue) {
        double hue = ((percentage * (endHue - startHue)) + startHue) / 360;

        double saturation = 1.0;
        double lightness = 0.5;

        return hslColorToRgb(hue, saturation, lightness);
    }

    public Color hslColorToRgb(double hue, double saturation, double lightness) {
        if (saturation == 0.0) {
            int grey = percToColor(lightness);
            return new Color(grey, grey, grey);
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

        return new Color(red, green, blue);
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
