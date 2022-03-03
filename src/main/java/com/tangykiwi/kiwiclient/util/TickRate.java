package com.tangykiwi.kiwiclient.util;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.GameJoinEvent;
import com.tangykiwi.kiwiclient.event.ReceivePacketEvent;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.util.Arrays;

import static java.lang.Float.NaN;

public class TickRate {
    private static final float[] tickRates = new float[20];
    private static int nextIndex = 0;
    private static long timeLastTimeUpdate = -1;
    private static long timeGameJoined;
    private static float lastTickRate;

    public TickRate() {
        lastTickRate = 20;
    }

    @Subscribe
    @AllowConcurrentEvents
    private static void onReceivePacket(ReceivePacketEvent event) {
        if (event.packet instanceof WorldTimeUpdateS2CPacket) {
            long now = System.currentTimeMillis();
            float timeElapsed = (float) (now - timeLastTimeUpdate) / 1000.0F;
            tickRates[nextIndex] = clamp(20.0f / timeElapsed, 0.0f, 20.0f);
            nextIndex = (nextIndex + 1) % tickRates.length;
            timeLastTimeUpdate = now;
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    private static void onGameJoin(GameJoinEvent event) {
        Arrays.fill(tickRates, 0);
        nextIndex = 0;
        timeGameJoined = timeLastTimeUpdate = System.currentTimeMillis();
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public static float getTickRate() {
        if (!Utils.canUpdate()) return 0;
        if (System.currentTimeMillis() - timeGameJoined < 4000) return 20;

        int numTicks = 0;
        float sumTickRates = 0.0f;
        for (float tickRate : tickRates) {
            if (tickRate > 0) {
                sumTickRates += tickRate;
                numTicks++;
            }
        }

        float tps = sumTickRates / numTicks;
        if(Float.isNaN(tps)) {
            return lastTickRate;
        }
        lastTickRate = tps;
        return tps;
    }
}
