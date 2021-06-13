package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.util.math.MatrixStack;

public class DrawOverlayEvent extends Event {

    public MatrixStack matrix;

    public DrawOverlayEvent(MatrixStack matrix) {
        this.matrix = matrix;
    }
}

