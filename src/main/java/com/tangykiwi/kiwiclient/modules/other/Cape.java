package com.tangykiwi.kiwiclient.modules.other;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.settings.ModeSetting;
import com.tangykiwi.kiwiclient.modules.settings.ToggleSetting;
import net.minecraft.util.Identifier;

public class Cape extends Module {
    private long lastFrameTime = 0L;
    private int lastFrame = 1;
    private int capeInterval = 100;

    public Cape() {
        super("Cape", "Gives you a custom cape", KEY_UNBOUND, Category.OTHER,
            new ModeSetting("Style", "Default", "Animated", "Gura", "AhriR34").withDesc("Cape to display"),
            new ToggleSetting("Glint", true).withDesc("Makes your cape enchanted"));
    }

    public Identifier getCape() {
        if (getSetting(0).asMode().mode == 0) {
            return KiwiClient.CAPE;
        } else if (getSetting(0).asMode().mode == 1) {
            return getFrame();
        } else if (getSetting(0).asMode().mode == 2) {
            return KiwiClient.CAPE2;
        }
        return KiwiClient.CAPE3;
    }

    private Identifier getFrame() {
        long time = System.currentTimeMillis();
        if (time > this.lastFrameTime + (long)this.capeInterval) {
            int currentFrameNo = this.lastFrame + 1 > 32 ? 1 : this.lastFrame + 1;
            this.lastFrame = currentFrameNo;
            this.lastFrameTime = time;
            return new Identifier(String.format("kiwiclient:textures/cosmetic/cape/gif/cape%d.png", currentFrameNo));
        } else {
            return new Identifier(String.format("kiwiclient:textures/cosmetic/cape/gif/cape%d.png", this.lastFrame));
        }
    }
}
