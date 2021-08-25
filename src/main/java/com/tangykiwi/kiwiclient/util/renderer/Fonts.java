/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package com.tangykiwi.kiwiclient.util.renderer;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.util.renderer.text.CustomTextRenderer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Fonts {
    private static final String[] BUILTIN_FONTS = { "product_sans.ttf" };
    public static final String DEFAULT_FONT = "product_sans";
    private static final File FOLDER = new File(KiwiClient.FOLDER, "font");

    public static CustomTextRenderer CUSTOM_FONT;

    private static String lastFont = "";

    public static void init() {
        FOLDER.mkdirs();

        // Copy built in fonts if they not exist
        for (String font : BUILTIN_FONTS) {
            File file = new File(FOLDER, font);
            if (!file.exists()) {
                copy(Fonts.class.getResourceAsStream("/assets/kiwiclient/font/" + font), file);
            }
        }

        // Load default font
        CUSTOM_FONT = new CustomTextRenderer(new File(FOLDER, DEFAULT_FONT + ".ttf"));
        lastFont = DEFAULT_FONT;
    }

    public static void copy(InputStream in, File to) {
        try {
            OutputStream out = new FileOutputStream(to);

            copy(in, out);

            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(InputStream in, OutputStream out) {
        byte[] bytes = new byte[512];
        int read;

        try {
            while ((read = in.read(bytes)) != -1) out.write(bytes, 0, read);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        lastFont = DEFAULT_FONT;
    }

    public static String[] getAvailableFonts() {
        List<String> fonts = new ArrayList<>(4);

        File[] files = FOLDER.listFiles(File::isFile);
        if (files != null) {
            for (File file : files) {
                int i = file.getName().lastIndexOf('.');
                if (file.getName().substring(i).equals(".ttf")) {
                    fonts.add(file.getName().substring(0, i));
                }
            }
        }

        return fonts.toArray(new String[0]);
    }
}
