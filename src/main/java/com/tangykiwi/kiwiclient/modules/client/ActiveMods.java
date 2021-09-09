package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import com.tangykiwi.kiwiclient.util.font.GlyphPageFontRenderer;
import com.tangykiwi.kiwiclient.util.font.IFont;
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
            GlyphPageFontRenderer textRenderer = IFont.CONSOLAS;
            //TextRenderer textRenderer = mc.textRenderer;

//            DrawableHelper.fill(e.matrix, 0, 60, textRenderer.getWidth(enabledMods.get(0).getName()) + 5, 62, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
//            for (Module m : enabledMods) {
//
//                int offset = count * (textRenderer.fontHeight + 1);
//
//                DrawableHelper.fill(e.matrix, textRenderer.getWidth(m.getName()) + 3, 62 + offset, textRenderer.getWidth(m.getName()) + 5, 63 + textRenderer.fontHeight + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
//                DrawableHelper.fill(e.matrix, 0, 62 + offset, textRenderer.getWidth(m.getName()) + 3, 63 + textRenderer.fontHeight + offset, 0x90000000);
//                textRenderer.draw(e.matrix, m.getName(), 2, 63 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
//
//                count++;
//            }

            int count = 0;
            ArrayList<Module> enabledMods = KiwiClient.moduleManager.getEnabledMods();
            DrawableHelper.fill(e.matrix, 0, 59, textRenderer.getStringWidth(enabledMods.get(0).getName()) + 5, 61, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
            for (Module m : enabledMods) {

                int offset = count * 8;

                DrawableHelper.fill(e.matrix, textRenderer.getStringWidth(m.getName()) + 3, 61 + offset, textRenderer.getStringWidth(m.getName()) + 5, 61 + 8 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
                DrawableHelper.fill(e.matrix, 0, 61 + offset, textRenderer.getStringWidth(m.getName()) + 3, 61 + 8 + offset, 0x90000000);
                textRenderer.drawString(e.matrix, m.getName(), 0.2, 61.2 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));

                count++;
            }
        }
    }
}
