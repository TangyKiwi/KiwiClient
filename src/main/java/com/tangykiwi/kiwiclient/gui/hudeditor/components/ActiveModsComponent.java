package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import java.util.ArrayList;
import java.util.Collections;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.DrawContext;

public class ActiveModsComponent extends HUDComponent {
    public ActiveModsComponent(float x, float y) {
        super("ActiveMods", x, y);
    }
    
    @Override
    public void render(DrawContext context) {
        super.render(context);

        // 0 = Left, 1 = Right
        // 0 = Up, 1 = Down
        int LR = KiwiClient.moduleManager.getModule("HUD").getSetting("ActiveMods").asToggle().getSetting("LR").asMode().getValue();
        int UD = KiwiClient.moduleManager.getModule("HUD").getSetting("ActiveMods").asToggle().getSetting("UD").asMode().getValue();

        ArrayList<Module> enabledMods = KiwiClient.moduleManager.getEnabledMods(fontRenderer);
        if (UD == 0) Collections.reverse(enabledMods);
        setX(LR == 0 ? 0 : KiwiClient.mc.currentScreen.width - fontRenderer.getStringWidth(enabledMods.get(0).getName()) - 1);
        setWidth((int) fontRenderer.getStringWidth(enabledMods.get(0).getName()));

        int curY = (int) getY();
        int colorOffset = 0;

        for (Module m : enabledMods) {
            int curX = LR == 0 ? 0 : (int) (KiwiClient.mc.currentScreen.width - fontRenderer.getStringWidth(m.getName()));
            fontRenderer.drawString(context, m.getName(), curX, curY, RenderUtils.getRainbow(4, 0.8f, 1, colorOffset * 150));
            curY += fontRenderer.getStringHeight(m.getName());
            colorOffset++;
        }

        setHeight(curY - (int) getY());
    }
}
