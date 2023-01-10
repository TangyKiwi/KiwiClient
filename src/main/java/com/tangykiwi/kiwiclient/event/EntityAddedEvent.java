package com.tangykiwi.kiwiclient.event;

import net.minecraft.entity.Entity;

public class EntityAddedEvent extends Event {
    public Entity entity;

    public EntityAddedEvent(Entity entity) {
        this.entity = entity;
    }
}
