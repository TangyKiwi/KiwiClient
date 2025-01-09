package com.tangykiwi.kiwiclient.util.font;

import com.tangykiwi.kiwiclient.KiwiClient;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontManager {

    private final Map<FontKey, FontRenderer> fontCache = new HashMap<>();

    public void init() throws IOException {
        for (Type type : Type.values()) {
            for (int size = 4; size <= 32; size++) {
                fontCache.put(new FontKey(size, type), create(size, type.getType()));
            }
        }
    }

    public FontRenderer create(float size, String name) throws IOException {
        String path = "assets/kiwiclient/fonts/" + name + ".ttf";

        try (InputStream inputStream = KiwiClient.class.getClassLoader().getResourceAsStream(path)) {
            Font[] font = Font.createFonts(Objects.requireNonNull(inputStream));

            return new FontRenderer(font, size, 256, 2);
        }
        catch (Exception e) {
            try {
                Font[] font = {new Font(name, Font.PLAIN, (int) size)};
                return new FontRenderer(font, size, 256, 2);
            } catch (Exception ee) {
                throw new RuntimeException(ee);
            }
        }
    }

    public FontRenderer getSize(int size, Type type) {
        return fontCache.computeIfAbsent(new FontKey(size, type), k -> {
            try {
                return create(size, type.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public FontRenderer getSize(int size, String name) {
        return fontCache.computeIfAbsent(new FontKey(size, Type.OTHER), k -> {
            try {
                return create(size, name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public enum Type {
        PRODUCT_SANS_BOLD("product_sans_bold"),
        PRODUCT_SANS_MEDIUM("product_sans_medium"),
        PRODUCT_SANS_REGULAR("product_sans_regular"),
        VERDANA("verdana_pro_regular"),
        SFUI("sfui_display_regular"),
        CONSOLAS("consola"),
        OTHER("other");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    private static final class FontKey {
        private final int size;
        private final Type type;

        private FontKey(int size, Type type) {
            this.size = size;
            this.type = type;
        }

        public int size() {
            return size;
        }

        public Type type() {
            return type;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (FontKey) obj;
            return this.size == that.size &&
                    Objects.equals(this.type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(size, type);
        }

        @Override
        public String toString() {
            return "FontKey[" +
                    "size=" + size + ", " +
                    "type=" + type + ']';
        }
    }
}
