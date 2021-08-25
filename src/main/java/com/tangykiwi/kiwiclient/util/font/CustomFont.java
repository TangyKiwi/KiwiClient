package com.tangykiwi.kiwiclient.util.font;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Math.round;
import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memSlice;

public class CustomFont {
    private final ByteBuffer ttf;
    private ByteBuffer bitmap;

    private final STBTTFontinfo info;

    private final int ascent;
    private final int descent;
    private final int lineGap;

    private final int BITMAP_W;
    private final int BITMAP_H;
    private final STBTTBakedChar.Buffer cdata;

    private final int fontHeight;
    private final float contentScaleX;
    private final float contentScaleY;

    public CustomFont(int height) {

        fontHeight = height;

        try {
            ttf = ioResourceToByteBuffer("/assets/kiwiclient/font/product_sans.ttf", 512 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to initialize font information.");
        }

        int bW = 512;
        int bH = 1024;
        int lH = height;

        byte[] bitmap = new byte[bW * bH];

        float scale = stbtt_ScaleForPixelHeight(info, lH);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

            ascent = round(pAscent.get(0) * scale);
            descent = round(pDescent.get(0) * scale);
            lineGap = round(pLineGap.get(0) * scale);
        }

        long monitor = glfwGetPrimaryMonitor();
        try (MemoryStack stack = stackPush()) {
            FloatBuffer px = stack.mallocFloat(1);
            FloatBuffer py = stack.mallocFloat(1);

            glfwGetMonitorContentScale(monitor, px, py);

            contentScaleX = px.get(0);
            contentScaleY = py.get(0);
        }

        BITMAP_W = round(512 * contentScaleX);
        BITMAP_H = round(512 * contentScaleY);

        cdata = init(BITMAP_W, BITMAP_H);
    }

    private void renderText(String text, int color) {
        int texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_W, BITMAP_H, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

        Color c = new Color(color);
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        glColor3f(r, g, b); // Text color

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        while (!glfwWindowShouldClose(MinecraftClient.getInstance().getWindow().getHandle())) {
            glfwPollEvents();

            glClear(GL_COLOR_BUFFER_BIT);

            float scaleFactor = 1.0F;

            glPushMatrix();
            // Zoom
            glScalef(scaleFactor, scaleFactor, 1f);
            // Scroll
            glTranslatef(4.0f, fontHeight * 0.5f + 4.0f, 0f);

            float scale = stbtt_ScaleForPixelHeight(info, fontHeight);

            try (MemoryStack stack = stackPush()) {
                IntBuffer pCodePoint = stack.mallocInt(1);

                FloatBuffer x = stack.floats(0.0f);
                FloatBuffer y = stack.floats(0.0f);

                STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);

                int lineStart = 0;

                float factorX = 1.0f / contentScaleY;
                float factorY = 1.0f / contentScaleY;

                float lineY = 0.0f;

                glBegin(GL_QUADS);
                for (int i = 0, to = text.length(); i < to; ) {
                    i += getCP(text, to, i, pCodePoint);

                    int cp = pCodePoint.get(0);
                    if (cp == '\n') {
                        y.put(0, lineY = y.get(0) + (ascent - descent + lineGap) * scale);
                        x.put(0, 0.0f);

                        lineStart = i;
                        continue;
                    } else if (cp < 32 || 128 <= cp) {
                        continue;
                    }

                    float cpX = x.get(0);
                    stbtt_GetBakedQuad(cdata, BITMAP_W, BITMAP_H, cp - 32, x, y, q, true);
                    x.put(0, scale(cpX, x.get(0), factorX));
                    if (i < to) {
                        getCP(text, to, i, pCodePoint);
                        x.put(0, x.get(0) + stbtt_GetCodepointKernAdvance(info, cp, pCodePoint.get(0)) * scale);
                    }

                    float
                            x0 = scale(cpX, q.x0(), factorX),
                            x1 = scale(cpX, q.x1(), factorX),
                            y0 = scale(lineY, q.y0(), factorY),
                            y1 = scale(lineY, q.y1(), factorY);

                    glTexCoord2f(q.s0(), q.t0());
                    glVertex2f(x0, y0);

                    glTexCoord2f(q.s1(), q.t0());
                    glVertex2f(x1, y0);

                    glTexCoord2f(q.s1(), q.t1());
                    glVertex2f(x1, y1);

                    glTexCoord2f(q.s0(), q.t1());
                    glVertex2f(x0, y1);
                }
                glEnd();
            }

            glPopMatrix();

            glfwSwapBuffers(MinecraftClient.getInstance().getWindow().getHandle());
        }

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    private static int getCP(String text, int to, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    private static float scale(float center, float offset, float factor) {
        return (offset - center) * factor + center;
    }

    private STBTTBakedChar.Buffer init(int BITMAP_W, int BITMAP_H) {
        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

        bitmap = BufferUtils.createByteBuffer(BITMAP_W * BITMAP_H);
        stbtt_BakeFontBitmap(ttf, fontHeight * contentScaleY, bitmap, BITMAP_W, BITMAP_H, 32, cdata);

        return cdata;
    }

    private ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                    InputStream source = this.getClass().getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();
        return memSlice(buffer);
    }

    private ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

}
