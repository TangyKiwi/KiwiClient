package com.tangykiwi.kiwiclient.event;

import net.minecraft.text.Text;

public class AddMessageEvent extends Event {
    public Text message;
    public int id;
    public boolean modified;

    public AddMessageEvent(Text message, int id) {
        this.setCancelled(false);
        this.message = message;
        this.id = id;
        this.modified = false;
    }
}
