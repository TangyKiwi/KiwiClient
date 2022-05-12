package com.tangykiwi.kiwiclient.modules.settings;

import com.tangykiwi.kiwiclient.gui.clickgui.window.ModuleWindow;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.font.IFont;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

public class BindSetting extends Setting<Integer> {

    private Module mod;

    public BindSetting(Module mod) {
        this.mod = mod;
        this.setDataValue(mod.getKeyCode());
        this.setHandler(SettingDataHandler.INTEGER);
    }

    public String getName() {
        return "Bind";
    }

    @Override
    public int render(ModuleWindow window, MatrixStack matrices, int x, int y, int len, int index, int max) {
        if (window.mouseOver(x, y, x + len, y + 12)) {
            DrawableHelper.fill(matrices, x + 1, y, x + len, y + 12, 0x70303070);
        }

        if (window.keyDown >= 0 && window.keyDown != GLFW.GLFW_KEY_ESCAPE && window.mouseOver(x, y, x + len, y + 12)) {
            mod.setKeyCode(window.keyDown == GLFW.GLFW_KEY_DELETE ? Module.KEY_UNBOUND : window.keyDown);
            this.setDataValue(mod.getKeyCode());
            MinecraftClient.getInstance().getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F, 0.3F));
        }

        String name = mod.getKeyCode() < 0 ? "NONE" : InputUtil.fromKeyCode(mod.getKeyCode(), -1).getLocalizedText().getString();
        if (name == null)
            name = "KEY" + mod.getKeyCode();
        else if (name.isEmpty())
            name = "NONE";

        IFont.CONSOLAS.drawStringWithShadow(matrices, "Bind: " + name + (window.mouseOver(x, y, x + len, y + 12) ? "..." : ""), x + 3, y + 2, 0xcfe0cf, 1);

        return index;
    }

    public BindSetting withDesc(String desc) {
        description = desc;
        return this;
    }

    @Override
    public int getHeight(int len) {
        return 12;
    }

    @Override
    public void setDataValue(Integer value) {
        this.mod.setKeyCode(value);
        super.setDataValue(value);
    }
}
