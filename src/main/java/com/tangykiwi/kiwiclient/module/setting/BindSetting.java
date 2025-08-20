package com.tangykiwi.kiwiclient.module.setting;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.lwjgl.glfw.GLFW;

import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import com.tangykiwi.kiwiclient.module.Module;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;

public class BindSetting extends Setting<Integer> {
    public BindSetting(int keyCode) {
        super("Bind", "Keybind for module");
        this.setValue(keyCode);
    }

    @Override
    public int render(DrawContext context, CategoryWindow window, int curYoffset) {
        int x = window.x;
        int y = window.y + curYoffset;
        int width = window.width;
        FontRenderer fontRenderer = window.fontRenderer;
        int fontHeight = (int) window.fontHeight;
        if (window.mouseOver(x, y, x + width, y + fontHeight)) {
            context.fill(x + 1, y, x + width, y + fontHeight, 0x70303070);
        }

        if (window.keyDown >= 0 && window.keyDown != GLFW.GLFW_KEY_ESCAPE && window.mouseOver(x, y, x + width, y + fontHeight)) {
            setValue(window.keyDown == GLFW.GLFW_KEY_DELETE ? Module.KEY_UNBOUND : window.keyDown);
            mc.getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.3F));
        }

        String name = getValue() < 0 ? "NONE" : InputUtil.fromKeyCode(getValue(), -1).getLocalizedText().getString();
        if (name == null) name = "KEY" + getValue();
        else if (name.isEmpty()) name = "NONE";

        fontRenderer.drawStringWithShadow(context, "Bind: " + name + (window.mouseOver(x, y, x + width, y + fontHeight) ? "..." : ""), x + 3, y + 2, 0xcfe0cf);

        return fontHeight + 1;
    }
}
