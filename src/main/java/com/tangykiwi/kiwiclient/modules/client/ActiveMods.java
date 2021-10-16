package com.tangykiwi.kiwiclient.modules.client;

import com.google.common.eventbus.AllowConcurrentEvents;
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
import java.util.HashMap;
import java.util.Map;

public class ActiveMods extends Module {

    public ArrayList<Module> currModules = new ArrayList<>();
    Map<Module, Integer> changingMods = new HashMap<>();
    boolean firstDraw = true;

    public ActiveMods() {
        super("ActiveMods", "Display toggled modules", KEY_UNBOUND, Category.CLIENT);
        super.toggle();
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        if(!mc.options.debugEnabled) {
            GlyphPageFontRenderer textRenderer = IFont.CONSOLAS;

            //TextRenderer textRenderer = mc.textRenderer;

//            DrawableHelper.fill(e.getMatrix(), 0, 60, textRenderer.getWidth(enabledMods.get(0).getName()) + 5, 62, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
//            for (Module m : enabledMods) {
//
//                int offset = count * (textRenderer.fontHeight + 1);
//
//                DrawableHelper.fill(e.getMatrix(), textRenderer.getWidth(m.getName()) + 3, 62 + offset, textRenderer.getWidth(m.getName()) + 5, 63 + textRenderer.fontHeight + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
//                DrawableHelper.fill(e.getMatrix(), 0, 62 + offset, textRenderer.getWidth(m.getName()) + 3, 63 + textRenderer.fontHeight + offset, 0x90000000);
//                textRenderer.draw(e.getMatrix(), m.getName(), 2, 63 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
//
//                count++;
//            }

            int count = 0;
            ArrayList<Module> enabledMods = KiwiClient.moduleManager.getEnabledMods();
            DrawableHelper.fill(e.getMatrix(), 0, 60, textRenderer.getStringWidth(enabledMods.get(0).getName()) + 5, 62, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));

            boolean changed = false;
            if(!firstDraw && enabledMods.size() > currModules.size()) {
                for(int i = 0; i < currModules.size(); i++) {
                    Module curMod = currModules.get(i);
                    Module enaMod = enabledMods.get(i);
                    if(!curMod.equals(enaMod)) {
                        changed = true;
                        changingMods.put(enaMod, -textRenderer.getStringWidth(enaMod.getName()));
                        break;
                    }
                }
                if(!changed) {
                    changingMods.put(enabledMods.get(currModules.size()), -textRenderer.getStringWidth(enabledMods.get(currModules.size()).getName()));
                }
            }

            for (Module m : enabledMods) {

                int offset = count * 8;

                if(!firstDraw && changingMods.containsKey(m) ) {
                    int displace = changingMods.get(m);
                    DrawableHelper.fill(e.getMatrix(), textRenderer.getStringWidth(m.getName()) + 3 + displace, 62 + offset, textRenderer.getStringWidth(m.getName()) + 5 + displace, 62 + 8 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
                    DrawableHelper.fill(e.getMatrix(), displace, 62 + offset, textRenderer.getStringWidth(m.getName()) + 3 + displace, 62 + 8 + offset, 0x90000000);
                    textRenderer.drawString(e.getMatrix(), m.getName(), 0.2 + displace, 62.2 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
                    if(displace + 1 == 0) {
                        changingMods.remove(m);
                    }
                    else
                    {
                        changingMods.replace(m, changingMods.get(m) + 1);
                    }
                }
                else{
                    DrawableHelper.fill(e.getMatrix(), textRenderer.getStringWidth(m.getName()) + 3, 62 + offset, textRenderer.getStringWidth(m.getName()) + 5, 62 + 8 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
                    DrawableHelper.fill(e.getMatrix(), 0, 62 + offset, textRenderer.getStringWidth(m.getName()) + 3, 62 + 8 + offset, 0x90000000);
                    textRenderer.drawString(e.getMatrix(), m.getName(), 0.2, 62.2 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
                }

                count++;
            }

            currModules = enabledMods;
        }
        firstDraw = false;
    }
}
