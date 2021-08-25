package com.tangykiwi.kiwiclient.modules.settings;

import com.tangykiwi.kiwiclient.gui.ModuleWindow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ToggleSetting extends Settings {

    private String name;
    private String description;
    public int value;
    public Boolean state;
    public int color;

    protected List<Settings> children = new ArrayList<>();
    protected boolean expanded = false;

    public ToggleSetting(String name, Boolean state) {
        this.name = name;
        this.state = state;
    }

    public ToggleSetting withDesc(String description) {
        this.description = description;
        return this;
    }

    public ToggleSetting withValue(int value) {
        this.value = value;
        return this;
    }

    public Settings getChild(int c) {
        return children.get(c);
    }

    public ToggleSetting withChildren(Settings... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    // public ToggleSetting withRange(int best, int good, int mid, int bad, int worst, Boolean reverse) {
    //     color = ColorUtil.getColorString(Integer.parseInt(value), best, good, mid, bad, worst, reverse);
    //     return this;
    // }

    public int getHeight(int len) {
        int h = 12;

        if (expanded) {
            h += 1;
            for (Settings s : children) h += s.getHeight(len - 2);
        }

        return h;
    }

    public void render(ModuleWindow window, MatrixStack matrices, int x, int y, int len) {
        String color2 = state ? "\u00a7a" : "\u00a7c";

        if (window.mouseOver(x, y, x + len, y + 12)) {
            DrawableHelper.fill(matrices, x + 1, y, x + len, y + 12, 0x70303070);
        }

        if (!children.isEmpty()) {
            if (window.rmDown && window.mouseOver(x, y, x + len, y + 12)) {
                expanded = !expanded;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F, 0.3F));
            }

            if (expanded) {
                DrawableHelper.fill(matrices, x + 2, y + 12, x + 3, y + getHeight(len) - 1, 0xff8070b0);

                int h = y + 12;
                for (Settings s : children) {
                    s.render(window, matrices, x + 2, h, len - 2);

                    h += s.getHeight(len - 3);
                }
            }

            if (expanded) {
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices,
                        color2 + "\u2228",
                        x + len - 8, y + 3, -1);
            } else {
                matrices.push();

                matrices.scale(0.75f, 0.75f, 1f);
                MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices,
                        color2 + "\u00a7l>",
                        (int) ((x + len - 7) * 1 / 0.75), (int) ((y + 4) * 1 / 0.75), -1);

                matrices.pop();
            }
        }

        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, color2 + name, x + 3, y + 2, 0xffffff);

        if (window.mouseOver(x, y, x + len, y + 12) && window.lmDown) {
            state = !state;
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F, 0.3F));
        }
    }

    public Triple<Integer, Integer, String> getGuiDesc(ModuleWindow window, int x, int y, int len) {
        if (!expanded || window.mouseY - y <= 12) return super.getGuiDesc(window, x, y, len);

        Triple<Integer, Integer, String> triple = null;

        int h = y + 12;
        for (Settings s : children) {
            if (window.mouseOver(x + 2, h, x + len, h + s.getHeight(len))) {
                triple = s.getGuiDesc(window, x + 2, h, len - 2);
            }

            h += s.getHeight(len - 2);
        }

        return triple;
    }

    public void toggle() {
        state = !state;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.description;
    }

    public int getValue() { return this.value; }
    
}
