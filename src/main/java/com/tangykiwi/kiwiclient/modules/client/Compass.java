package com.tangykiwi.kiwiclient.modules.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.List;

public class Compass extends Module {

    public float innerWidth;
    public float outerWidth;
    public boolean shadow;
    public float scale;
    public int accuracy;

    public List<Degree> degrees = Lists.newArrayList();

    public Compass(float i, float o, float s, int a, boolean sh){
        super("Compass", "Renders a compass at the top of the screen", KEY_UNBOUND, Category.CLIENT);
        innerWidth = i;
        outerWidth = o;
        scale = s;
        accuracy = a;
        shadow = sh;


        degrees.add(new Degree("N", 1));
        degrees.add(new Degree("195", 2));
        degrees.add(new Degree("210", 2));
        degrees.add(new Degree("NE", 3));
        degrees.add(new Degree("240", 2));
        degrees.add(new Degree("255", 2));
        degrees.add(new Degree("E", 1));
        degrees.add(new Degree("285", 2));
        degrees.add(new Degree("300", 2));
        degrees.add(new Degree("SE", 3));
        degrees.add(new Degree("330", 2));
        degrees.add(new Degree("345", 2));
        degrees.add(new Degree("S", 1));
        degrees.add(new Degree("15", 2));
        degrees.add(new Degree("30", 2));
        degrees.add(new Degree("SW", 3));
        degrees.add(new Degree("60", 2));
        degrees.add(new Degree("75", 2));
        degrees.add(new Degree("W", 1));
        degrees.add(new Degree("105", 2));
        degrees.add(new Degree("120", 2));
        degrees.add(new Degree("NW", 3));
        degrees.add(new Degree("150", 2));
        degrees.add(new Degree("165", 2));
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        if(shadow){
            RenderSystem.enableBlend();
            RenderSystem.setShaderTexture(0, new Identifier("kiwiclient:textures/hud/shadow.png"));
            mc.currentScreen.drawTexture(e.getMatrix(), (int) (mc.getWindow().getScaledWidth() * 0.1), 0, 0.0F, 0.0F, (int) (mc.getWindow().getScaledWidth() * 0.8), 40, (int) (mc.getWindow().getScaledWidth() * 0.8), 40);
            RenderSystem.disableBlend();
        }

        RenderSystem.enableBlend();

        float center = mc.getWindow().getScaledWidth() / 2;

        int count = 0;

        float yaw = (MathHelper.wrapDegrees(mc.getCameraEntity().getYaw()) + 180) * 2 + 360 * 2;

        for(Degree d : degrees){

            float location = center + ( count * 30 ) - yaw;
            float completeLocation = location - 2 - IFont.CONSOLAS.getStringWidth(d.text) / 2;

            int opacity = opacity(completeLocation);
            // custom text renderer has a stroke when rendering "out of bounds" transparent things
            String print = opacity == 0 ? "" : d.text;

            if(d.type == 1 && opacity != 16777215){
                IFont.CONSOLAS.drawString(e.getMatrix(), print, completeLocation, -90 + 100, opacity, 1);
            }

            if(d.type == 2 && opacity != 16777215){
                DrawableHelper.fill(e.getMatrix(), (int) (location - 1), -90 + 100 + 4, (int) (location + 1), -90 + 105 + 4, opacity);
                IFont.CONSOLAS.drawString(e.getMatrix(), print, completeLocation, -90 + 105 + 3.5f + 4, opacity, 1);
            }

            if(d.type == 3 && opacity != 16777215){
                IFont.CONSOLAS.drawString(e.getMatrix(), print, completeLocation, -90 + 100 + IFont.CONSOLAS.getFontHeight()/2 - IFont.CONSOLAS.getFontHeight()/2, opacity, 1);
            }

            count++;
        }
        for(Degree d : degrees){

            float location = center + ( count * 30 ) - yaw;
            float completeLocation = location - 2 - IFont.CONSOLAS.getStringWidth(d.text) / 2;

            int opacity = opacity(completeLocation);
            String print = opacity == 0 ? "" : d.text;

            if(d.type == 1){
                IFont.CONSOLAS.drawString(e.getMatrix(), print, completeLocation, -90 + 100, opacity, 1);
            }

            if(d.type == 2){
                DrawableHelper.fill(e.getMatrix(), (int) (location - 1), -90 + 100 + 4, (int) (location + 1), -90 + 105 + 4, opacity);
                IFont.CONSOLAS.drawString(e.getMatrix(), print, completeLocation, -90 + 105 + 3.5f + 4, opacity, 1);
            }

            if(d.type == 3){
                IFont.CONSOLAS.drawString(e.getMatrix(), print, completeLocation, -90 + 100 + IFont.CONSOLAS.getFontHeight()/2 - IFont.CONSOLAS.getFontHeight()/2, opacity, 1);
            }

            count++;
        }
        for(Degree d : degrees){

            float location = center + ( count * 30 ) - yaw;
            float completeLocation = location - 2 - IFont.CONSOLAS.getStringWidth(d.text) / 2;
            
            int opacity = opacity(completeLocation);
            String print = opacity == 0 ? "" : d.text;

            if(d.type == 1){
                IFont.CONSOLAS.drawString(e.getMatrix(), print, completeLocation, -90 + 100, opacity, 1);
            }

            if(d.type == 2){
                DrawableHelper.fill(e.getMatrix(), (int) (location - 1), -90 + 100 + 4, (int) (location + 1), -90 + 105 + 4, opacity);
                IFont.CONSOLAS.drawString(e.getMatrix(), print, completeLocation, -90 + 105 + 3.5f + 4, opacity, 1);
            }

            if(d.type == 3){
                IFont.CONSOLAS.drawString(e.getMatrix(), print, completeLocation, -90 + 100 + IFont.CONSOLAS.getFontHeight()/2 - IFont.CONSOLAS.getFontHeight()/2, opacity, 1);
            }

            count++;
        }
        RenderSystem.disableBlend();
    }

    public int opacity(float offset){
        if(offset < mc.getWindow().getScaledWidth() * 0.15 || offset > mc.getWindow().getScaledWidth() * 0.85) {
            return 0;
        }
        float offs = (float) (255 - 255 * Math.abs(mc.getWindow().getScaledWidth() / 2 - offset) / (mc.getWindow().getScaledWidth() * 0.4));
        Color c = new Color(255, 255, 255, (int) offs);

        return c.getRGB();
    }

}

class Degree {

    public String text;
    public int type;

    public Degree(String s, int t){
        text = s;
        type = t;
    }
}
