package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.gui.DrawContext;

public class DrawOverlayEvent extends Event {

    private DrawContext context;

    public DrawOverlayEvent(DrawContext context) {
        this.context = context;
    }

    public DrawContext getContext() {
        return context;
    }
}

