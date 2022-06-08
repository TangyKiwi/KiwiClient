package com.tangykiwi.kiwiclient.event;

public class KeyPressEvent extends Event {
    private int key;
    private int scanCode;
    private int action;

    public KeyPressEvent(int key, int scanCode, int action) {
        this.key = key;
        this.scanCode = scanCode;
        this.action = action;
    }

    public int getKeyCode() {
        return key;
    }

    public int getScanCode() {
        return scanCode;
    }

    public int getAction() { return action; }
}