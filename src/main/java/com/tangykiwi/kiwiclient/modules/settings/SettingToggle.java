package com.tangykiwi.kiwiclient.modules.settings;

import com.tangykiwi.kiwiclient.gui.ModuleWindow;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;


public class SettingToggle extends Settings {

    private String name;
    private String description;
    private String value;
    public Boolean state;
    public int color;

    protected List<Settings> children = new ArrayList<>();
    protected boolean expanded = false;

    public SettingToggle(String name, Boolean state) {
        this.name = name;
        this.state = state;
    }

    public SettingToggle withDesc(String description) {
        this.description = description;
        return this;
    }

    public SettingToggle withValue(String value) {
        this.value = value;
        return this;
    }

    public SettingToggle withRange(int best, int good, int mid, int bad, int worst, Boolean reverse) {
        color = ColorUtil.getColorString(Integer.parseInt(value), best, good, mid, bad, worst, reverse);
        return this;
    }

    public int getHeight(int len) {
        int h = 12;

        if (expanded) {
            h += 1;
            for (Settings s : children) h += s.getHeight(len - 2);
        }

        return h;
    }

    public void render(ModuleWindow window, MatrixStack matrix, int x, int y, int len) {
        String color2;

        if (state) {
            if (window.mouseOver(x, y, x + len, y + 12)) color2 = "\u00a72";
            else color2 = "\u00a7a";
        } else {
            if (window.mouseOver(x, y, x + len, y + 12)) color2 = "\u00a74";
            else color2 = "\u00a7c";
        }

        if (!children.isEmpty()) {
            if (window.rmDown && window.mouseOver(x, y, x + len, y + 12)) expanded = !expanded;

			/*if (expanded) {
				window.fillGreySides(x + 1, y, x + len - 2, y + 12);
				window.fillGreySides(x, y + 11, x + len - 1, y + getHeight(len));
				DrawableHelper.fill(x, y, x + len - 3, y + 1, 0x90000000);
				DrawableHelper.fill(x + 1, y + getHeight(len) - 2, x + 2, y + getHeight(len) - 1, 0x90000000);
				DrawableHelper.fill(x + 2, y + getHeight(len) - 1, x + len - 2, y + getHeight(len), 0x90b0b0b0);

				int h = y + 12;
				for (SettingBase s: children) {
					s.render(window, x + 1, h, len - 2);

					h += s.getHeight(len - 2);
				}
			}*/
            if (expanded) {
                DrawableHelper.fill(matrix, x + 2, y + 12, x + 3, y + getHeight(len) - 1, 0x90b0b0b0);

                int h = y + 12;
                for (Settings s : children) {
                    s.render(window, matrix, x + 2, h, len - 2);

                    h += s.getHeight(len - 2);
                }
            }

            GL11.glPushMatrix();
            GL11.glScaled(0.65, 0.65, 1);
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrix,
                    color2 + (expanded ? "[\u00a7lv" + color2 + "]" : "[\u00a7l>" + color2 + "]"), (int) ((x + len - 13) * 1 / 0.65), (int) ((y + 4) * 1 / 0.65), -1);
            GL11.glPopMatrix();
        }


        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrix, color2 + name, x + 3, y + 2, 0xffffff);

        if (window.mouseOver(x, y, x + len, y + 12) && window.lmDown) state = !state;
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
    
}
