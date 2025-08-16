package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

import net.minecraft.client.gui.DrawContext;

public class IPComponent extends HUDComponent {
    public IPComponent(float x, float y) {
        super("IP", x, y);
    }

    @Override
    public void render(DrawContext context) {
        super.render(context);
        
        String renderString = "IP: Singleplayer";
        if(mc.getCurrentServerEntry() != null) {
            if(mc.getCurrentServerEntry().isRealm()) {
                renderString = "IP: " + mc.getCurrentServerEntry().name;
            }
            else renderString = "IP: " + mc.getCurrentServerEntry().address;
        }
        setWidth((int) fontRenderer.getStringWidth(renderString));        
        fontRenderer.drawString(context, renderString, getX(), getY(), 0xFFAA00);
    }
    
}
