package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.DrawContext;

public class IPComponent extends HUDComponent {
    public IPComponent(float x, float y) {
        super("IP", x, y);
    }

    @Override
    public void render(DrawContext context, FontRenderer fontRenderer) {
        String ip = "IP: Singleplayer";
        if(mc.getCurrentServerEntry() != null) {
            if(mc.getCurrentServerEntry().isRealm()) {
                ip = "IP: " + mc.getCurrentServerEntry().name;
            }
            else ip = "IP: " + mc.getCurrentServerEntry().address;
        }        
        fontRenderer.drawString(context, "IP: " + ip, getX(), getY(), 0xFFAA00);
    }
    
}
