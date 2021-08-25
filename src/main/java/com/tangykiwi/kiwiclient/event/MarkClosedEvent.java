package com.tangykiwi.kiwiclient.event;

public class MarkClosedEvent extends Event {

    public MarkClosedEvent() {
        this.setCancelled(false);
    }
}
