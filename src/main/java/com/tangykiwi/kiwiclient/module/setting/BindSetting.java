package com.tangykiwi.kiwiclient.module.setting;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import org.lwjgl.glfw.GLFW;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.platform.InputConstants;
import com.tangykiwi.kiwiclient.gui.clickgui.CategoryWindow;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import com.tangykiwi.kiwiclient.module.Module;

public class BindSetting extends Setting<Integer> {
    public BindSetting(int keyCode) {
        super("Bind", "Keybind for module");
        this.setValue(keyCode);
    }

    @Override
    public void render(GuiGraphicsExtractor context, CategoryWindow window, int curYoffset) {
        int x = window.x;
        int y = window.y + curYoffset;
        int width = window.width;
        FontRenderer fontRenderer = window.fontRenderer;
        int fontHeight = (int) window.fontHeight;

        context.fill(x + 1, y + 1, x + 2, y + fontHeight + 1, 0xff8070b0);

        if (window.mouseOver(x, y + 1, x + width, y + fontHeight + 1)) {
            context.fill(x + 1, y + 1, x + width - 1, y + fontHeight + 1, 0x70303070);
        }

        if (window.keyDown >= 0 && window.keyDown != GLFW.GLFW_KEY_ESCAPE && window.mouseOver(x, y, x + width, y + fontHeight)) {
            setValue(window.keyDown == GLFW.GLFW_KEY_DELETE ? Module.KEY_UNBOUND : window.keyDown);
            mc.getSoundManager().play(
                SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 0.3F));
        }

        String name = getValue() < 0 ? "NONE" : InputConstants.getKey(new KeyEvent(getValue(), -1, -1)).getName();
        if (name == null) name = "KEY" + getValue();
        else if (name.isEmpty()) name = "NONE";

        fontRenderer.drawString(context, "Bind: " + name + (window.mouseOver(x, y, x + width, y + fontHeight) ? "..." : ""), x + 3, y + 2, 0xcfe0cf);

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
