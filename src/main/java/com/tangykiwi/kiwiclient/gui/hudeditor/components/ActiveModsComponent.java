package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ActiveModsComponent extends HUDComponent {
    public ActiveModsComponent(float x, float y) {
        super("ActiveMods", x, y);
    }

    ArrayList<Module> fakeMods = new ArrayList<>(List.of(
        new Module("Active Mods", "", Category.OTHER),
        new Module("will be", "", Category.OTHER),
        new Module("shown", "", Category.OTHER),
        new Module("here", "", Category.OTHER)
    ));
    
    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);

        // 0 = Left, 1 = Right
        // 0 = Up, 1 = Down
        int LR = KiwiClient.moduleManager.getModule("HUD").getSetting("ActiveMods").asToggle().getChild("LR").asMode().getValue();
        int UD = KiwiClient.moduleManager.getModule("HUD").getSetting("ActiveMods").asToggle().getChild("UD").asMode().getValue();

        ArrayList<Module> enabledMods = KiwiClient.moduleManager.getEnabledMods(fontRenderer);
        if (UD == 0) Collections.reverse(enabledMods);
        if (enabledMods.isEmpty()) {
            enabledMods = fakeMods;
        }
        setX((LR == 0) ? 0 : KiwiClient.mc.getWindow().getGuiScaledWidth() - fontRenderer.getStringWidth(enabledMods.get(0).getName()) - 2);
        setWidth((int) fontRenderer.getStringWidth(enabledMods.get(0).getName()));

        int curY = (int) getY();
        int colorOffset = 0;

        for (Module m : enabledMods) {
            int curX = ((LR == 0) ? 0 : (int) (KiwiClient.mc.getWindow().getGuiScaledWidth() - fontRenderer.getStringWidth(m.getName()) - 2));
            fontRenderer.drawString(context, m.getName(), curX, curY, RenderUtils.getRainbow(4, 0.8f, 1, colorOffset * 150));
            curY += fontRenderer.getStringHeight(m.getName());
            colorOffset++;
        }

        setHeight(curY - (int) getY());
    }
}
