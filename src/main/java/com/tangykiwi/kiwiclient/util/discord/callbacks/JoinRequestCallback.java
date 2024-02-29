package com.tangykiwi.kiwiclient.util.discord.callbacks;

import com.sun.jna.Callback;
import com.tangykiwi.kiwiclient.util.discord.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(final DiscordUser p0);
}
