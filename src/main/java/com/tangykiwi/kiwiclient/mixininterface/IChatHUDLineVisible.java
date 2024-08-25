package com.tangykiwi.kiwiclient.mixininterface;

public interface IChatHUDLineVisible extends IChatHUDLine {
    boolean kiwiclient$isStartOfEntry();
    void kiwiclient$setStartOfEntry(boolean start);
}