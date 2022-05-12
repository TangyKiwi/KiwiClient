package com.tangykiwi.kiwiclient.modules.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tangykiwi.kiwiclient.gui.clickgui.window.ModuleWindow;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class ToggleSetting extends Setting<Boolean> {

    public boolean state;
    public String text;
    public int value;
    int index = 0, max = 0;

    protected boolean defaultState;

    protected List<Setting> children = new ArrayList<>();
    protected boolean expanded = false;

    public ToggleSetting(String text, boolean state) {
        this.state = state;
        this.text = text;

        defaultState = state;

        this.setDataValue(this.state);
        this.setHandler(SettingDataHandler.BOOLEAN);
    }

    public String getName() {
        return text;
    }

    public int render(ModuleWindow window, MatrixStack matrices, int x, int y, int len, int index, int max) {
        this.index = index;
        this.max = max;
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
                for (Setting s : children) {
                    s.render(window, matrices, x + 2, h, len - 2, index, max);
                    index++;

                    if(index == max) return max;

                    h += s.getHeight(len - 3);
                }
            }

            if (expanded) {
                IFont.CONSOLAS.drawString(matrices,
                        color2 + "v",
                        x + len - 8, y + 2, -1, 1);
            } else {
                IFont.CONSOLAS.drawStringWithShadow(matrices,
                        color2 + "\u00a7l>",
                        x + len - 8, y + 2, -1, 1);
            }
        }

        IFont.CONSOLAS.drawStringWithShadow(matrices, color2 + text, x + 3, y + 2, 0xffffff, 1);

        if (window.mouseOver(x, y, x + len, y + 12) && window.lmDown) {
            state = !state;
            this.setDataValue(state);
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F, 0.3F));
        }

        return index;
    }

    public int getHeight(int len) {
        int h = 12;

        if (expanded) {
            h += 1;
            for (Setting s : children)
                h += s.getHeight(len - 2);
                index++;
                if(index == max) return h;
        }

        return h;
    }

    public Setting getChild(int c) {
        return children.get(c);
    }

    public ToggleSetting withChildren(Setting... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    public ToggleSetting withDesc(String desc) {
        description = desc;
        return this;
    }

    public ToggleSetting withValue(int value) {
        this.value = value;
        return this;
    }

    public Triple<Integer, Integer, String> getGuiDesc(ModuleWindow window, int x, int y, int len) {
        if (!expanded || window.mouseY - y <= 12)
            return super.getGuiDesc(window, x, y, len);

        Triple<Integer, Integer, String> triple = null;

        int h = y + 12;
        for (Setting s : children) {
            if (window.mouseOver(x + 2, h, x + len, h + s.getHeight(len))) {
                triple = s.getGuiDesc(window, x + 2, h, len - 2);
            }

            h += s.getHeight(len - 2);
        }

        return triple;
    }

    public List<Setting> getChildren() {
        return children;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public int getValue() { return this.value; }

    @Override
    public void read(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject jo = json.getAsJsonObject();
            if (jo.has("toggled")) {
                super.read(jo.get("toggled"));
            }

            for (Map.Entry<String, JsonElement> e : jo.get("children").getAsJsonObject().entrySet()) {
                for (Setting<?> s : children) {
                    if (s.getName().equals(e.getKey())) {
                        s.read(e.getValue());
                        break;
                    }
                }
            }
        } else {
            super.read(json);
        }
    }

    @Override
    public JsonElement write() {
        if (children.isEmpty()) {
            return super.write();
        }

        JsonObject jo = new JsonObject();
        jo.add("toggled", super.write());

        JsonObject subJo = new JsonObject();
        for (Setting<?> s : children) {
            subJo.add(s.getName(), s.write());
        }

        jo.add("children", subJo);
        return jo;
    }

    @Override
    public void setDataValue(Boolean value) {
        this.state = value;
        super.setDataValue(value);
    }
}
