package com.tangykiwi.kiwiclient.modules.render;

import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.event.DrawOverlayEvent;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;

import java.util.ArrayList;

public class ActiveMods extends Module {

    public ActiveMods() {
        super("ActiveMods", "Display toggle modules", KEY_UNBOUND, Category.RENDER);
    }

    @Subscribe
    public void onDrawOverlay(DrawOverlayEvent e) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        //Style CUSTOM_STYLE = Style.EMPTY.withFont(new Identifier("kiwiclient", "titillium"));
        int scaledWidth = client.getWindow().getScaledWidth();
        int scaledHeight = client.getWindow().getScaledHeight();

        int count = 0;
        ArrayList<Module> enabledMods = KiwiClient.moduleManager.getEnabledMods();
        for(Module m : enabledMods) {

            int offset = count * (textRenderer.fontHeight + 6);

            DrawableHelper.fill(e.matrix, scaledWidth - textRenderer.getWidth(m.getName()) - 10, offset, scaledWidth - textRenderer.getWidth(m.getName()) - 8, 6 + textRenderer.fontHeight + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));
            DrawableHelper.fill(e.matrix, scaledWidth - textRenderer.getWidth(m.getName()) - 8, offset, scaledWidth, 6 + textRenderer.fontHeight + offset, 0x90000000);
            textRenderer.draw(e.matrix, m.getName(), scaledWidth - textRenderer.getWidth(m.getName()) - 2, 4 + offset, ColorUtil.getRainbow(4, 0.8f, 1, count * 150));

            count++;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
