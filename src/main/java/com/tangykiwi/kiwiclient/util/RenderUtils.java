package com.tangykiwi.kiwiclient.util;

import com.tangykiwi.kiwiclient.KiwiClient;

public class RenderUtils {
    public static int getGuiScale() {
        return (int) KiwiClient.mc.getWindow().getScaleFactor();
    }
}
