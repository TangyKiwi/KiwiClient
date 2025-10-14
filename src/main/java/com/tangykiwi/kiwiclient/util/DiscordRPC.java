package com.tangykiwi.kiwiclient.util;

import static com.tangykiwi.kiwiclient.KiwiClient.LOGGER;

import java.time.Instant;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityButton;

public class DiscordRPC {
    public Activity activity = new Activity();

    private static Core core;

    public void start() {
        final CreateParams params = new CreateParams();
        params.setClientID(790758093113917491L);
        params.setFlags(CreateParams.Flags.NO_REQUIRE_DISCORD);
        activity.timestamps().setStart(Instant.now());

        try (Core core = new Core(params)) {
            DiscordRPC.core = core;
            activity.assets().setLargeImage("discord_background");
            activity.setDetails("Loading");
            activity.addButton(new ActivityButton("Download", "https://github.com/TangyKiwi/KiwiClient"));
            update();
        } catch (RuntimeException e) {
            LOGGER.error("Failed to start Discord RPC");
            e.printStackTrace();
        }
    }

    public void update() {
        core.activityManager().updateActivity(activity);
    }

    public void shutdown() {
        core.close();
    }
}
