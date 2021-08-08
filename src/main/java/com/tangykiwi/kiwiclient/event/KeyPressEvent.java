package com.tangykiwi.kiwiclient.event;

public class KeyPressEvent extends Event {
    private int key;
    private int scanCode;

    public KeyPressEvent(int key, int scanCode) {
        this.key = key;
        this.scanCode = scanCode;
    }

    public int getKeyCode() {
        return key;
    }

    public int getScanCode() {
        return scanCode;
    }
}