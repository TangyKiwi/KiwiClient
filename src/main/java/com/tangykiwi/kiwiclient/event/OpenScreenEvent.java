package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.gui.screens.Screen;

public class OpenScreenEvent extends Event {

    private Screen screen;

    public OpenScreenEvent(Screen screen) {
        this.screen = screen;
    }

    public Screen getScreen() {
        return screen;
    }
}