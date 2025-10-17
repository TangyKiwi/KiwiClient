package com.tangykiwi.kiwiclient.util;

import static com.tangykiwi.kiwiclient.KiwiClient.LOGGER;

import java.time.Instant;

import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.CreateParams;
import de.jcm.discordgamesdk.LogLevel;
import de.jcm.discordgamesdk.activity.Activity;
import de.jcm.discordgamesdk.activity.ActivityButton;
import de.jcm.discordgamesdk.activity.ActivityButtonsMode;

public class DiscordRPC {
    public final Activity activity = new Activity();

    private static Core core;

    public void start() {
        CreateParams params = new CreateParams();
        params.setClientID(790758093113917491L);
        params.setFlags(CreateParams.Flags.SUPPRESS_EXCEPTIONS);

        core = new Core(params);
        core.setLogHook(LogLevel.DEBUG, (level, message) -> LOGGER.info(message));

        activity.timestamps().setStart(Instant.now());
        activity.setActivityButtonsMode(ActivityButtonsMode.BUTTONS);
        activity.addButton(new ActivityButton("Download", "https://github.com/TangyKiwi/KiwiClient"));
        activity.assets().setLargeImage("discord_background");
        activity.setDetails("Loading");

        update();
    }

    public void update() {
        core.activityManager().updateActivity(activity);
    }

    public void shutdown() {
        core.close();
    }
}
