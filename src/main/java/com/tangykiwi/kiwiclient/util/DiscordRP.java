package com.tangykiwi.kiwiclient.util;

import com.tangykiwi.kiwiclient.KiwiClient;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordRP {

    private boolean running = true;
    private static long time = 0;

    public void start() {
        this.time = System.currentTimeMillis();

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
            @Override
            public void apply(DiscordUser user) {
                System.out.println(user.username + "#" +  user.discriminator);
                update("Loading ", KiwiClient.name + " v" + KiwiClient.version);
            }
        }).build();

        DiscordRPC.discordInitialize("790758093113917491", handlers, true);

        new Thread("Discord RPC Callback") {
            @Override
            public void run(){
                while(running) {
                    DiscordRPC.discordRunCallbacks();
                }
            }
        }.start();
    }

    public void shutdown() {
        running = false;
        DiscordRPC.discordShutdown();
    }

    public void update(String firstLine, String secondLine) {
        DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondLine);
        b.setBigImage("discord_background", "");
        b.setDetails(firstLine);
        b.setStartTimestamps(time);

        DiscordRPC.discordUpdatePresence(b.build());
    }
}
