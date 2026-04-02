package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class DrawOverlayEvent extends Event {

    private GuiGraphicsExtractor context;

    public DrawOverlayEvent(GuiGraphicsExtractor context) {
        this.context = context;
    }

    public GuiGraphicsExtractor getContext() {
        return context;
    }
}