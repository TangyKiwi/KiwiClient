package com.tangykiwi.kiwiclient.event;

import com.tangykiwi.kiwiclient.mixininterface.IClientPlayerEntity;

public class OnMoveEvent extends Event {
    private final IClientPlayerEntity player;

    public OnMoveEvent(IClientPlayerEntity player)
    {
        this.player = player;

    }

    public IClientPlayerEntity getPlayer() { return player; }
}
