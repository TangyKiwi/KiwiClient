package com.tangykiwi.kiwiclient.event;

import net.minecraft.entity.Entity;

public class AddEntityPrivateEvent extends Event {
    public Entity entity;

    public AddEntityPrivateEvent(Entity entity) {
        this.entity = entity;
    }
}
