package com.tangykiwi.kiwiclient.module.setting;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class ToggleSetting extends Setting<Boolean> {
    // value = boolean enabled

    protected List<Setting<?>> children = new ArrayList<>();
    protected boolean expanded = false;

    public ToggleSetting(String name, String desc) {
        super(name, desc);
        this.setValue(false);
    }

    public ToggleSetting(String name, String desc, boolean enabled) {
        super(name, desc);
        this.setValue(enabled);
    }

    public ToggleSetting withChildren(Setting<?>... children) {
        this.children.addAll(Arrays.asList(children));
        return this;
    }

    public Setting<?> getChild(String name) {
        for (Setting<?> s : children) {
            if (s.getName().equals(name)) return s;
        }
        return null;
    }

    public Setting<?> getChild(int index) {
        return children.get(index);
    }

    @Override
    public void render(DrawContext context, CategoryWindow window, int curYoffset) {
        int x = window.x;
        int y = window.y + curYoffset;
        int width = window.width;
        FontRenderer fontRenderer = window.fontRenderer;
        int fontHeight = (int) window.fontHeight;
        height = fontHeight + 1;

        context.fill(x + 1, y + 1, x + 2, y + fontHeight + 1, 0xff8070b0);

        if (window.mouseOver(x, y + 1, x + width, y + fontHeight + 1)) {
            context.fill(x + 1, y + 1, x + width - 1, y + fontHeight + 1, 0x70303070);
        }

        String color = getValue() ? "\u00a7a" : "\u00a7c";
        fontRenderer.drawString(context, color + this.getName(), x + 3, y + 2, -1);

        if (window.lmDown && window.mouseOver(x, y, x + width, y + fontHeight)) {
            setValue(!getValue());
            mc.getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.3F));
        }

        if (!children.isEmpty()) {
            if (window.rmDown && window.mouseOver(x, y, x + width, y + fontHeight)){
                expanded = !expanded;
                mc.getSoundManager().play(
                    PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.3F));
            }

            if (expanded) {
                fontRenderer.drawString(context, color + "v", x + width - 8, y + 2, -1);
                context.fill(x + 2, y + fontHeight, x + 3, y + getHeight() - 1, 0xff8070b0);
                for (Setting<?> s : children) {
                    s.render(context, window, curYoffset + height);
                    height += s.getHeight();
                }
            } else {
                fontRenderer.drawString(context, color + "\u00a7l>", x + width - 8, y + 2, -1);
            }
        }
    }

    @Override
    public void read(JsonElement je) {
        if (je.isJsonObject()) {
            JsonObject jo = je.getAsJsonObject();
            setValue(jo.get("toggled").getAsBoolean());
            expanded = jo.get("expanded").getAsBoolean();

            JsonObject subJo = jo.getAsJsonObject("children");
            for (Setting<?> s : children) {
                if (subJo.has(s.getName())) {
                    s.read(subJo.get(s.getName()));
                }
            }
        }
        else {
            setValue(je.getAsBoolean());
        }
    }

    @Override
    public JsonElement write() {
        if (children.isEmpty()) {
            return new JsonPrimitive(getValue());
        }

        JsonObject jo = new JsonObject();
        jo.add("toggled", new JsonPrimitive(getValue()));
        jo.add("expanded", new JsonPrimitive(expanded));

        JsonObject subJo = new JsonObject();
        for (Setting<?> s : children) {
            subJo.add(s.getName(), s.write());
        }
        jo.add("children", subJo);
        return jo;
    }
}
