package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;

import java.util.ArrayList;

public class ActiveMods extends Module {

    public ActiveMods() {
        super("ActiveMods", "Display toggled modules", KEY_UNBOUND, Category.CLIENT);
        super.toggle();
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        if(!mc.options.debugEnabled) {
            TextRenderer textRenderer = mc.textRenderer;
            //Style CUSTOM_STYLE = Style.EMPTY.withFont(new Identifier("kiwiclient", "titillium"));
            int scaledWidth = mc.getWindow().getScaledWidth();
            int scaledHeight = mc.getWindow().getScaledHeight();

            int count = 0;
            ArrayList<Module> enabledMods = KiwiClient.moduleManager.getEnabledMods();
            DrawableHelper.fill(e.matrix, scaledWidth - textRenderer.getWidth(enabledMods.get(0).getName()) - 5, 0, scaledWidth, 2, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
            for (Module m : enabledMods) {

                int offset = count * (textRenderer.fontHeight + 1);

                DrawableHelper.fill(e.matrix, scaledWidth - textRenderer.getWidth(m.getName()) - 5, 2 + offset, scaledWidth - textRenderer.getWidth(m.getName()) - 3, 3 + textRenderer.fontHeight + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
                DrawableHelper.fill(e.matrix, scaledWidth - textRenderer.getWidth(m.getName()) - 3, 2 + offset, scaledWidth, 3 + textRenderer.fontHeight + offset, 0x90000000);
                textRenderer.draw(e.matrix, m.getName(), scaledWidth - textRenderer.getWidth(m.getName()) - 1, 3 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));

                count++;
            }
        }
    }
}
