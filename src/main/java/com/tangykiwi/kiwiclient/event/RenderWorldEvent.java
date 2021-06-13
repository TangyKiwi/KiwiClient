package com.tangykiwi.kiwiclient.event;

public class RenderWorldEvent extends Event {
    public float tickDelta;
    public double offsetX, offsetY, offsetZ;

    public RenderWorldEvent(float tickDelta, double offsetX, double offsetY, double offsetZ) {
        this.tickDelta = tickDelta;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }
}
