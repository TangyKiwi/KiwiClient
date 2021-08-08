package com.tangykiwi.kiwiclient.event;

public class MouseButtonEvent extends Event {
    private int key;

    public MouseButtonEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
