package com.tangykiwi.kiwiclient.util.font;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.util.RenderUtils;
import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.Closeable;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A simple font renderer. Supports multiple fonts, passed in as {@link Font} instances.
 * <h2>Constructor</h2>
 * {@link FontRenderer#FontRenderer(Font[], float)} takes in a {@link Font} array with the fonts to use. All fonts in this array will be asked, in order,
 * if they have a glyph corresponding to the one being drawn. If none of them have it, the missing "square" is drawn.
 */
public class FontRenderer implements Closeable {
    private static final Char2IntArrayMap colorCodes = new Char2IntArrayMap() {{
        put('0', 0x000000);
        put('1', 0x0000AA);
        put('2', 0x00AA00);
        put('3', 0x00AAAA);
        put('4', 0xAA0000);
        put('5', 0xAA00AA);
        put('6', 0xFFAA00);
        put('7', 0xAAAAAA);
        put('8', 0x555555);
        put('9', 0x5555FF);
        put('A', 0x55FF55);
        put('B', 0x55FFFF);
        put('C', 0xFF5555);
        put('D', 0xFF55FF);
        put('E', 0xFFFF55);
        put('F', 0xFFFFFF);
    }};
    //	private static final int BLOCK_SIZE = 256;
    private static final Object2ObjectArrayMap<Identifier, ObjectList<DrawEntry>> GLYPH_PAGE_CACHE = new Object2ObjectArrayMap<>();
    private final float originalSize;
    private final ObjectList<GlyphMap> maps = new ObjectArrayList<>();
    private final Char2ObjectArrayMap<Glyph> allGlyphs = new Char2ObjectArrayMap<>();
    private int scaleMul = 0;
    private Font[] fonts;
    private int previousGameScale = -1;
    private final int charsPerPage;
    private final int padding;

    /**
     * Initializes a new FontRenderer with the specified fonts
     *
     * @param fonts                    The fonts to use. The font renderer will go over each font in this array, search for the glyph, and render it if found. If no font has the specified glyph, it will draw the missing font symbol.
     * @param sizePx                   The size of the font in minecraft pixel units. One pixel unit = `guiScale` pixels
     * @param charactersPerPage        How many characters one glyph page should contain. Default 256
     * @param paddingBetweenCharacters Padding between characters on a glyph page. Increase if font characters tend to have a lot of decoration around the "main body" of a character.
     */
    public FontRenderer(Font[] fonts, float sizePx, int charactersPerPage, int paddingBetweenCharacters) {
        Preconditions.checkArgument(fonts.length > 0, "fonts.length == 0");
        Preconditions.checkArgument(charactersPerPage > 4, "Unreasonable charactersPerPage count");
        Preconditions.checkArgument(paddingBetweenCharacters > 0, "paddingBetweenCharacters > 0");
        this.originalSize = sizePx;
        this.charsPerPage = charactersPerPage;
        this.padding = paddingBetweenCharacters;
        init(fonts, sizePx);
    }

    /**
     * Initializes a new FontRenderer with the specified fonts. Equivalent to {@link FontRenderer#FontRenderer(Font[], float, int, int) FontRenderer}{@code (fonts, sizePx, 256, 5)}
     *
     * @param fonts  The fonts to use. The font renderer will go over each font in this array, search for the glyph, and render it if found. If no font has the specified glyph, it will draw the missing font symbol.
     * @param sizePx The size of the font in minecraft pixel units. One pixel unit = `guiScale` pixels
     */
    public FontRenderer(Font[] fonts, float sizePx) {
        this(fonts, sizePx, 256, 5);
    }

    private static int floorNearestMulN(int x, int n) {
        return n * (int) Math.floor((double) x / (double) n);
    }

    /**
     * Strips all characters prefixed with a § from the given string
     *
     * @param text The string to strip
     * @return The stripped string
     */
    public static String stripControlCodes(String text) {
        char[] chars = text.toCharArray();
        StringBuilder f = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '§') {
                i++;
                continue;
            }
            f.append(c);
        }
        return f.toString();
    }

    private void sizeCheck() {
        int gs = RenderUtils.getGuiScale();
        if (gs != this.previousGameScale) {
            close();
            init(this.fonts, this.originalSize);
        }
    }

    private void init(Font[] fonts, float sizePx) {
        this.previousGameScale = RenderUtils.getGuiScale();
        this.scaleMul = this.previousGameScale;
        this.fonts = new Font[fonts.length];
        for (int i = 0; i < fonts.length; i++) {
            this.fonts[i] = fonts[i].deriveFont(sizePx * this.scaleMul);
        }
    }

    private GlyphMap generateMap(char from, char to) {
        GlyphMap gm = new GlyphMap(from, to, this.fonts, Identifier.of("renderer", "temp/" + IntStream.range(0, 32)
                .mapToObj(operand -> String.valueOf((char) new Random().nextInt('a', 'z' + 1)))
                .collect(Collectors.joining())), padding);
        maps.add(gm);
        return gm;
    }

    private Glyph locateGlyph0(char glyph) {
        for (GlyphMap map : maps) { // go over existing ones
            if (map.contains(glyph)) { // do they have it? good
                return map.getGlyph(glyph);
            }
        }
        int base = floorNearestMulN(glyph, charsPerPage); // if not, generate a new page and return the generated glyph
        GlyphMap glyphMap = generateMap((char) base, (char) (base + charsPerPage));
        return glyphMap.getGlyph(glyph);
    }

    private Glyph locateGlyph1(char glyph) {
        return allGlyphs.computeIfAbsent(glyph, this::locateGlyph0);
    }

    public static int[] RGBIntToRGB(int in) {
        int red = in >> 8 * 2 & 0xFF;
        int green = in >> 8 & 0xFF;
        int blue = in & 0xFF;
        return new int[]{red, green, blue};
    }

    /**
     * Draws a string
     *
     * @param stack The MatrixStack
     * @param s     The string to draw
     * @param x     X coordinate to draw at
     * @param y     Y coordinate to draw at
     * @param color Texts color
     */
    public void drawString(MatrixStack stack, String s, float x, float y, Color color) {
        float r = (float) color.getRed() / 255;
        float g = (float) color.getGreen() / 255;
        float b = (float) color.getBlue() / 255;
        float a = (float) color.getAlpha() / 255;
        sizeCheck();
        float r2 = r, g2 = g, b2 = b;
        stack.push();
        stack.translate(x, y, 0);
        stack.scale(1f / this.scaleMul, 1f / this.scaleMul, 1f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
        BufferBuilder bb;
        Matrix4f mat = stack.peek().getPositionMatrix();
        char[] chars = s.toCharArray();
        float xOffset = 0;
        float yOffset = 0;
        boolean inSel = false;
        int lineStart = 0;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (inSel) {
                inSel = false;
                char c1 = Character.toUpperCase(c);
                if (colorCodes.containsKey(c1)) {
                    int ii = colorCodes.get(c1);
                    int[] col = RGBIntToRGB(ii);
                    r2 = col[0] / 255f;
                    g2 = col[1] / 255f;
                    b2 = col[2] / 255f;
                } else if (c1 == 'R') {
                    r2 = r;
                    g2 = g;
                    b2 = b;
                }
                continue;
            }
            if (c == '§') {
                inSel = true;
                continue;
            } else if (c == '\n') {
                yOffset += getStringHeight(s.substring(lineStart, i)) * scaleMul;
                xOffset = 0;
                lineStart = i + 1;
                continue;
            }
            Glyph glyph = locateGlyph1(c);
            if (glyph.value() != ' ') {
                Identifier i1 = glyph.owner().bindToTexture;
                DrawEntry entry = new DrawEntry(xOffset, yOffset, r2, g2, b2, glyph);
                GLYPH_PAGE_CACHE.computeIfAbsent(i1, integer -> new ObjectArrayList<>()).add(entry);
            }
            xOffset += glyph.width();
        }
        for (Identifier identifier : GLYPH_PAGE_CACHE.keySet()) {
            RenderSystem.setShaderTexture(0, identifier);
            List<DrawEntry> objects = GLYPH_PAGE_CACHE.get(identifier);

            bb = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

            int len = objects.size();
            int i = 0;
            for (DrawEntry object : objects) {
                float mult = 1;
                // mult is test code for cutting off rendering vertically
//                if (i < len / 2) {
//                    mult = 0.5F;
//                }
                i++;
                float xo = object.atX;
                float yo = object.atY;
                float cr = object.r;
                float cg = object.g;
                float cb = object.b;
                Glyph glyph = object.toDraw;
                GlyphMap owner = glyph.owner();
                float w = glyph.width();
                float h = glyph.height();
                float u1 = (float) glyph.u() / owner.width;
                float v1 = (float) glyph.v() / owner.height;
                float u2 = (float) (glyph.u() + glyph.width()) / owner.width;
                float v2 = (float) (glyph.v() + glyph.height() * mult) / owner.height;

                bb.vertex(mat, xo + 0, yo + h * mult, 0).texture(u1, v2).color(cr, cg, cb, a);
                bb.vertex(mat, xo + w, yo + h * mult, 0).texture(u2, v2).color(cr, cg, cb, a);
                bb.vertex(mat, xo + w, yo + 0, 0).texture(u2, v1).color(cr, cg, cb, a);
                bb.vertex(mat, xo + 0, yo + 0, 0).texture(u1, v1).color(cr, cg, cb, a);
            }
            BufferRenderer.drawWithGlobalProgram(bb.end());
        }

        stack.pop();
        GLYPH_PAGE_CACHE.clear();
    }

    /**
     * Draws a string centered on the X coordinate
     *
     * @param stack The MatrixStack
     * @param s     The string to draw
     * @param x     X center coordinate of the text to draw
     * @param y     Y coordinate of the text to draw
     * @param color Texts color
     */
    public void drawCenteredString(MatrixStack stack, String s, float x, float y, Color color) {
        drawString(stack, s, x - getStringWidth(s) / 2f, y, color);
    }

    /**
     * Draws a string with shadow
     *
     * @param stack The MatrixStack
     * @param s     The string to draw
     * @param x     X coordinate to draw at
     * @param y     Y coordinate to draw at
     * @param color Texts color
     */
    public void drawStringWithShadow(MatrixStack stack, String s, float x, float y, Color color) {
        int c = color.getRGB();
        drawString(stack, s, x + 1.0F, y + 1.0F, new Color((c & 16579836) >> 2 | c & -16777216));
        drawString(stack, s, x, y, color);
    }

    /**
     * Draws a string centered on the X coordinate with a shadow
     *
     * @param stack The MatrixStack
     * @param s     The string to draw
     * @param x     X center coordinate of the text to draw
     * @param y     Y coordinate of the text to draw
     * @param color Texts color
     */
    public void drawCenteredStringWithShadow(MatrixStack stack, String s, float x, float y, Color color) {
        int c = color.getRGB();
        float width = getStringWidth(s);
        drawString(stack, s, x - width / 2f + 1.0F, y + 1.0F, new Color((c & 16579836) >> 2 | c & -16777216));
        drawString(stack, s, x - width / 2f, y, color);
    }

    /**
     * Calculates the width of the string, if it were drawn on the screen
     *
     * @param text The text to simulate
     * @return The width of the string if it'd be drawn on the screen
     */
    public float getStringWidth(String text) {
        char[] c = stripControlCodes(text).toCharArray();
        float currentLine = 0;
        float maxPreviousLines = 0;
        for (char c1 : c) {
            if (c1 == '\n') {
                maxPreviousLines = Math.max(currentLine, maxPreviousLines);
                currentLine = 0;
                continue;
            }
            Glyph glyph = locateGlyph1(c1);
            currentLine += glyph.width() / (float) this.scaleMul;
        }
        return Math.max(currentLine, maxPreviousLines);
    }

    /**
     * Calculates the height of the string, if it were drawn on the screen. This is necessary, because the fonts in this FontRenderer might have a different height for each char.
     *
     * @param text The text to simulate
     * @return The height of the string if it'd be drawn on the screen
     */
    public float getStringHeight(String text) {
        char[] c = stripControlCodes(text).toCharArray();
        if (c.length == 0) {
            c = new char[]{' '};
        }
        float currentLine = 0;
        float previous = 0;
        for (char c1 : c) {
            if (c1 == '\n') {
                if (currentLine == 0) {
                    // empty line, assume space
                    currentLine = locateGlyph1(' ').height() / (float) this.scaleMul;
                }
                previous += currentLine;
                currentLine = 0;
                continue;
            }
            Glyph glyph = locateGlyph1(c1);
            currentLine = Math.max(glyph.height() / (float) this.scaleMul, currentLine);
        }
        return currentLine + previous;
    }

    /**
     * Clears all glyph maps, and unlinks them. The font can continue to be used, but it will have to regenerate the maps.
     */
    @Override
    public void close() {
        for (GlyphMap map : maps) {
            map.destroy();
        }
        maps.clear();
        allGlyphs.clear();
    }

    static final class DrawEntry {
        private final float atX;
        private final float atY;
        private final float r;
        private final float g;
        private final float b;
        private final Glyph toDraw;

        DrawEntry(float atX, float atY, float r, float g, float b, Glyph toDraw) {
            this.atX = atX;
            this.atY = atY;
            this.r = r;
            this.g = g;
            this.b = b;
            this.toDraw = toDraw;
        }

        public float atX() {
            return atX;
        }

        public float atY() {
            return atY;
        }

        public float r() {
            return r;
        }

        public float g() {
            return g;
        }

        public float b() {
            return b;
        }

        public Glyph toDraw() {
            return toDraw;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (DrawEntry) obj;
            return Float.floatToIntBits(this.atX) == Float.floatToIntBits(that.atX) &&
                    Float.floatToIntBits(this.atY) == Float.floatToIntBits(that.atY) &&
                    Float.floatToIntBits(this.r) == Float.floatToIntBits(that.r) &&
                    Float.floatToIntBits(this.g) == Float.floatToIntBits(that.g) &&
                    Float.floatToIntBits(this.b) == Float.floatToIntBits(that.b) &&
                    Objects.equals(this.toDraw, that.toDraw);
        }

        @Override
        public int hashCode() {
            return Objects.hash(atX, atY, r, g, b, toDraw);
        }

        @Override
        public String toString() {
            return "DrawEntry[" +
                    "atX=" + atX + ", " +
                    "atY=" + atY + ", " +
                    "r=" + r + ", " +
                    "g=" + g + ", " +
                    "b=" + b + ", " +
                    "toDraw=" + toDraw + ']';
        }
    }
}
