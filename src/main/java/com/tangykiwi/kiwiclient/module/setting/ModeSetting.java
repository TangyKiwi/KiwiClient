package com.tangykiwi.kiwiclient.module.setting;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class ModeSetting extends Setting<Integer> {
    public String[] modes;
    // value = index of current mode

    public ModeSetting(String name, String desc, String[] modes) {
        super(name, desc);
        this.modes = modes;
        this.setValue(0);
    }

    public ModeSetting(String name, String desc, String[] modes, int index) {
        super(name, desc);
        this.modes = modes;
        this.setValue(index);
    }

    @Override
    public void render(DrawContext context, CategoryWindow window, int curYoffset) {
        int x = window.x;
        int y = window.y + curYoffset;
        int width = window.width;
        FontRenderer fontRenderer = window.fontRenderer;
        int fontHeight = (int) window.fontHeight;

        context.fill(x + 1, y, x + 2, y + fontHeight + 1, 0xff8070b0);

        if (window.mouseOver(x, y + 1, x + width, y + fontHeight + 1)) {
            context.fill(x + 1, y + 1, x + width - 1, y + fontHeight + 1, 0x70303070);
        }

        fontRenderer.drawString(context, getName() + ": " + modes[getValue()], x + 3, y + 2, -1);

        if (window.mouseOver(x, y, x + width, y + fontHeight) && window.lmDown) {
            setValue((getValue() + 1) % modes.length);
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.3F));
        }

        height = fontHeight + 1;
    }

    @Override
    public void read(JsonElement je) {
        setValue(je.getAsInt());
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(getValue());
    }
}
