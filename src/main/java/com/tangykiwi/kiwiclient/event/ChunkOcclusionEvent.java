package com.tangykiwi.kiwiclient.event;

public class ChunkOcclusionEvent extends Event {
    private static final ChunkOcclusionEvent INSTANCE = new ChunkOcclusionEvent();

    public static ChunkOcclusionEvent get() {
        INSTANCE.setCancelled(false);
        return INSTANCE;
    }
}
