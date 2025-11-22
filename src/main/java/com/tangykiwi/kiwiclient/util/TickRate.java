package com.tangykiwi.kiwiclient.util;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.tangykiwi.kiwiclient.event.PacketEvent;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class TickRate {
    public final float[] tickRates = new float[20];
    public int nextIndex = 0;
    public long timeLastTimeUpdate = -1;
    public long timeGameJoined;
    private float lastTickRate;

    public TickRate() {
        lastTickRate = 20;
    }

    @Subscribe
    @AllowConcurrentEvents
    private void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof WorldTimeUpdateS2CPacket) {
            long now = System.currentTimeMillis();
            float timeElapsed = (now - timeLastTimeUpdate) / 1000.0F;
            tickRates[nextIndex] = clamp(20.0f / timeElapsed, 0.0f, 20.0f);
            nextIndex = (nextIndex + 1) % tickRates.length;
            timeLastTimeUpdate = now;
        }
    }

    public float clamp(float value, float min, float max) {
        if (value < min) return min;
        return Math.min(value, max);
    }

    public float getTickRate() {
        if (mc.world == null) return 0;
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
