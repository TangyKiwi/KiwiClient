package com.tangykiwi.kiwiclient.modules.settings;

import com.tangykiwi.kiwiclient.gui.clickgui.window.ModuleWindow;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;

public class ModeSetting extends Settings {
    public String[] modes;
    public int mode;
    public String text;

    public ModeSetting(String text, String... modes) {
        this.modes = modes;
        this.text = text;
    }

    public int getNextMode() {
        if (mode + 1 >= modes.length) {
            return 0;
        }

        return mode + 1;
    }

    public String getName() {
        return text;
    }

    public int getHeight(int len) {
        return 12;
    }

    public int render(ModuleWindow window, MatrixStack matrices, int x, int y, int len, int index, int max) {
        if (window.mouseOver(x, y, x + len, y + 12)) {
            DrawableHelper.fill(matrices, x + 1, y, x + len, y + 12, 0x70303070);
        }

        IFont.CONSOLAS.drawStringWithShadow(matrices, text + ": " + modes[mode], x + 3, y + 2, 0xcfe0cf);

        if (window.mouseOver(x, y, x + len, y + 12) && window.lmDown) {
            mode = getNextMode();
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F, 0.3F));
        }

        return index;
    }

    public ModeSetting withDesc(String desc) {
        description = desc;
        return this;
    }
}
