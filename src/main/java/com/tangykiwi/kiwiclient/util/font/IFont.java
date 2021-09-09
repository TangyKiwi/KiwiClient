package com.tangykiwi.kiwiclient.util.font;

public class IFont {
    public static GlyphPageFontRenderer customFont = GlyphPageFontRenderer.createFromID("/assets/kiwiclient/fonts/JetBrains Mono.ttf",
            25, false, false, false);

    public static GlyphPageFontRenderer customFontBold = GlyphPageFontRenderer.createFromID("/assets/kiwiclient/fonts/product_sans.ttf",
            9, true, false, false);

    public static GlyphPageFontRenderer CONSOLAS = GlyphPageFontRenderer.create("Consolas",
            16, false, false, false);
}
