package com.tangykiwi.kiwiclient.mixininterface;

import com.mojang.authlib.GameProfile;

public interface IChatHUDLine {
    String kiwiclient$getText();

    int kiwiclient$getId();

    void kiwiclient$setId(int id);

    GameProfile kiwiclient$getSender();

    void kiwiclient$setSender(GameProfile profile);
}
