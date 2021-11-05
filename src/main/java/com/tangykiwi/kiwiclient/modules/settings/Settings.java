package com.tangykiwi.kiwiclient.modules.settings;

import com.tangykiwi.kiwiclient.gui.clickgui.window.ModuleWindow;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.commons.lang3.tuple.Triple;

public abstract class Settings {
    
    protected String description = "";

    public ModeSetting asMode() {
        try {
            return (ModeSetting) this;
        } catch (Exception e) {
            throw new ClassCastException("Exception parsing setting: " + this);
        }
    }

    public ToggleSetting asToggle() {
        try {
            return (ToggleSetting) this;
        } catch (Exception e) {
            throw new ClassCastException("Exception parsing setting: " + this);
        }
    }

    public SliderSetting asSlider() {
        try {
            return (SliderSetting) this;
        } catch (Exception e) {
            throw new ClassCastException("Exception parsing setting: " + this);
        }
    }

    public Triple<Integer, Integer, String> getGuiDesc(ModuleWindow window, int x, int y, int len) {
        return Triple.of(x + len + 2, y, description);
    }

    public abstract String getName();

    public abstract int getHeight(int len);

    public abstract int render(ModuleWindow window, MatrixStack matrices, int x, int y, int len, int index, int max);

    public String getDesc() {
        return description;
    }
}
