package com.tangykiwi.kiwiclient.event;

import net.minecraft.client.input.KeyEvent;

public class KeyPressEvent extends Event {
    private KeyEvent keyInput;
    private int key;
    private int scanCode;
    private int modifiers;
    private int action;

    public KeyPressEvent(KeyEvent keyInput, int action) {
        this.keyInput = keyInput;
        this.key = keyInput.key();
        this.scanCode = keyInput.scancode();
        this.modifiers = keyInput.modifiers();
        this.action = action;
    }

    public KeyEvent getKeyInput() {
        return keyInput;
    }

    public int getKeyCode() {
        return key;
    }

    public int getScanCode() {
        return scanCode;
    }

    public int getAction() {
        return action;
    }

    public int getModifiers() {
        return modifiers;
    }
}
