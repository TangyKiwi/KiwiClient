package com.tangykiwi.kiwiclient.util.font;

public class IFont {
    public static GlyphPageFontRenderer customFont = GlyphPageFontRenderer.createFromID("/assets/kiwiclient/font/product_sans.ttf",
            90, false, false, false);

    public static GlyphPageFontRenderer customFontBold = GlyphPageFontRenderer.createFromID("/assets/kiwiclient/font/product_sans.ttf",
            90, true, false, false);
}
