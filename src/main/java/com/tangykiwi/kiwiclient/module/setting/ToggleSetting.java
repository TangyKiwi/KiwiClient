package com.tangykiwi.kiwiclient.module.setting;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

public class ToggleSetting extends Setting<Boolean> {
    // value = boolean enabled

    public ToggleSetting(String name, String desc) {
        super(name, desc);
        this.setValue(false);
    }

    public ToggleSetting(String name, String desc, boolean enabled) {
        super(name, desc);
        this.setValue(enabled);
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

        String color = getValue() ? "\u00a7a" : "\u00a7c";
        fontRenderer.drawString(context, color + this.getName(), x + 3, y + 2, -1);

        if (window.lmDown && window.mouseOver(x, y, x + width, y + fontHeight)) {
            setValue(!getValue());
            mc.getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.3F));
        }

        height = fontHeight + 1;
    }
}
