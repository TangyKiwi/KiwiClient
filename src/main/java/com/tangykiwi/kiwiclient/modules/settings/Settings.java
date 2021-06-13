package com.tangykiwi.kiwiclient.modules.settings;

import com.tangykiwi.kiwiclient.gui.ModuleWindow;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.commons.lang3.tuple.Triple;

public abstract class Settings {
    
    protected String description = "";

    public com.tangykiwi.kiwiclient.modules.settings.ToggleSetting asToggle() {
        try {
            return (com.tangykiwi.kiwiclient.modules.settings.ToggleSetting) this;
        } catch (Exception e) {
            throw new ClassCastException("Exception parsing setting: " + this);
        }
    }

    public com.tangykiwi.kiwiclient.modules.settings.SliderSetting asSlider() {
        try {
            return (com.tangykiwi.kiwiclient.modules.settings.SliderSetting) this;
        } catch (Exception e) {
            throw new ClassCastException("Execption parsing setting: " + this);
        }
    }

    public Triple<Integer, Integer, String> getGuiDesc(ModuleWindow window, int x, int y, int len) {
        return Triple.of(x + len + 2, y, description);
    }

    public abstract String getName();

    public abstract int getHeight(int len);

    public abstract void render(ModuleWindow window, MatrixStack matrix, int x, int y, int len);

    public String getDesc() {
        return description;
    }
}
