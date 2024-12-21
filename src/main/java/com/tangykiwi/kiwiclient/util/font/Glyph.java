package com.tangykiwi.kiwiclient.util.font;

import java.util.Objects;

final class Glyph {
    private final int u;
    private final int v;
    private final int width;
    private final int height;
    private final char value;
    private final GlyphMap owner;

    Glyph(int u, int v, int width, int height, char value, GlyphMap owner) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.value = value;
        this.owner = owner;
    }

    public int u() {
        return u;
    }

    public int v() {
        return v;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public char value() {
        return value;
    }

    public GlyphMap owner() {
        return owner;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Glyph) obj;
        return this.u == that.u &&
                this.v == that.v &&
                this.width == that.width &&
                this.height == that.height &&
                this.value == that.value &&
                Objects.equals(this.owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v, width, height, value, owner);
    }

    @Override
    public String toString() {
        return "Glyph[" +
                "u=" + u + ", " +
                "v=" + v + ", " +
                "width=" + width + ", " +
                "height=" + height + ", " +
                "value=" + value + ", " +
                "owner=" + owner + ']';
    }
}
