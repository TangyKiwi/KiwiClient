package com.tangykiwi.kiwiclient.module.other;

import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.setting.ModeSetting;
import com.tangykiwi.kiwiclient.module.setting.ToggleSetting;
import com.tangykiwi.kiwiclient.util.Textures;

import net.minecraft.util.Identifier;

public class Cape extends Module {
    private long lastFrameTime = 0L;
    private int lastFrame = 1;
    private int capeInterval = 100;

    public Cape() {
        super("Cape", "Custom cape", Category.OTHER,
            new ModeSetting("Style", "Cape to display", "Default", "Animated", "Gura", "AhriR34"),
            new ToggleSetting("Glint", "Enchanted cape effect", true));
    }

    public Identifier getCape() {
        if (getSetting(0).asMode().getValue() == 0) {
            return Textures.CAPE;
        } else if (getSetting(0).asMode().getValue() == 1) {
            return getFrame();
        } else if (getSetting(0).asMode().getValue() == 2) {
            return Textures.CAPE2;
        }
        return Textures.CAPE3;
    }

    private Identifier getFrame() {
        long time = System.currentTimeMillis();
        if (time > this.lastFrameTime + (long)this.capeInterval) {
            int currentFrameNo = this.lastFrame + 1 > 32 ? 1 : this.lastFrame + 1;
            this.lastFrame = currentFrameNo;
            this.lastFrameTime = time;
            return Identifier.of(String.format("kiwiclient:textures/cosmetic/cape/gif/cape%d.png", currentFrameNo));
        } else {
            return Identifier.of(String.format("kiwiclient:textures/cosmetic/cape/gif/cape%d.png", this.lastFrame));
        }
    }

    // handling done in CapeFeatureRendererMixin
}
