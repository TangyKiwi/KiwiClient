package com.tangykiwi.kiwiclient.util;

import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static MinecraftClient mc;

    public static byte[] readBytes(File file) {
        try {
            InputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            byte[] buffer = new byte[256];
            int read;
            while ((read = in.read(buffer)) > 0) out.write(buffer, 0, read);

            in.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public static boolean canUpdate() {
        return mc != null && mc.world != null && mc.player != null;
    }
}
