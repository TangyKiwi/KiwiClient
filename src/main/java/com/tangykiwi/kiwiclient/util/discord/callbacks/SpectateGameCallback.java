package com.tangykiwi.kiwiclient.util.discord.callbacks;

import com.sun.jna.Callback;

public interface SpectateGameCallback extends Callback {
    void apply(final String p0);
}
