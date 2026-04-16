package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class IPComponent extends HUDComponent {
    public IPComponent(float x, float y) {
        super("IP", x, y);
    }

    @Override
    public void render(GuiGraphicsExtractor context) {
        super.render(context);
        
        String renderString = "IP: Singleplayer";
        if(mc.getCurrentServer() != null) {
            if(mc.getCurrentServer().isRealm()) {
                renderString = "IP: " + mc.getCurrentServer().name;
            }
            else renderString = "IP: " + mc.getCurrentServer().ip;
        }
        setWidth((int) fontRenderer.getStringWidth(renderString));        
        fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
    }
    
}
