package com.tangykiwi.kiwiclient.util;

import net.minecraft.client.MinecraftClient;

public class Utils {
    public static MinecraftClient mc;

    public static boolean canUpdate() {
        return mc != null && (mc.world != null || mc.player != null);
    }
}
