package com.tangykiwi.kiwiclient.event;

public class SendChatMessageEvent extends Event {
    public String message;

    public SendChatMessageEvent(String message) {
        this.message = message;
    }
}
