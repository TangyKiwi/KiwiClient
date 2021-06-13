package com.tangykiwi.kiwiclient.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.util.ArrayList;

public class Window {

    public int x1;
    public int y1;
    public int x2;
    public int y2;

    public String title;
    public ItemStack icon;

    public boolean closed;
    public boolean selected = false;

    public ArrayList<WindowButton> buttons = new ArrayList<>();

    private boolean dragging = false;
    private int dragOffX;
    private int dragOffY;

    public int inactiveTime = 0;

    public Window(int x1, int y1, int x2, int y2, String title, ItemStack icon) {
        this(x1, y1, x2, y2, title, icon, false);
    }

    public Window(int x1, int y1, int x2, int y2, String title, ItemStack icon, boolean closed) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.title = title;
        this.icon = icon;
        this.closed = closed;
    }

    public void render(MatrixStack matrix, int mX, int mY) {
        TextRenderer textRend = MinecraftClient.getInstance().textRenderer;

        if (dragging) {
            x1 = Math.max(0, mX - dragOffX);
            y1 = Math.max(0, mY - dragOffY);
            x2 = (x2 - x1) + mX - dragOffX - Math.min(0, mX - dragOffX);
            y2 = (y2 - y1) + mY - dragOffY - Math.min(0, mY - dragOffY);
        }

        drawBar(matrix, mX, mY, textRend);

        for (WindowButton w : buttons) {
            int bx1 = x1 + w.x1;
            int by1 = y1 + w.y1;
            int bx2 = x1 + w.x2;
            int by2 = y1 + w.y2;

            DrawableHelper.fill(matrix, bx1, by1, bx2 - 1, by2 - 1, 0xffb0b0b0);
            DrawableHelper.fill(matrix, bx1 + 1, by1 + 1, bx2, by2, 0xff000000);
            DrawableHelper.fill(matrix, bx1 + 1, by1 + 1, bx2 - 1, by2 - 1,
                    selected && mX >= bx1 && mX <= bx2 && mY >= by1 && mY <= by2 ? 0xff959595 : 0xff858585);
            textRend.drawWithShadow(matrix, w.text, bx1 + (bx2 - bx1) / 2 - textRend.getWidth(w.text) / 2, by1 + (by2 - by1) / 2 - 4, -1);
        }

        /* window icon */
        boolean drawIcon = true;
        if (drawIcon) {
            RenderSystem.getModelViewStack().push();
            RenderSystem.getModelViewStack().scale(0.6f, 0.6f, 1f);

            DiffuseLighting.enableGuiDepthLighting();
            MinecraftClient.getInstance().getItemRenderer().renderInGui(
                    icon, (int) ((x1 + (drawIcon ? 3 : 2)) * 1 / 0.6), (int) ((y1 + 2) * 1 / 0.6));
            DiffuseLighting.disableGuiDepthLighting();

            RenderSystem.getModelViewStack().pop();
            RenderSystem.applyModelViewMatrix();
        }

        /* window title */
        int iconWidth = drawIcon ? (icon.getItem() == Items.ARMOR_STAND || icon.getItem() == Items.POTION ? 13 : 14) : 4;
        textRend.drawWithShadow(matrix, title, x1 + iconWidth, y1 + 3, -1);
        if (inactiveTime >= 0) {
            inactiveTime--;
        }
    }

    protected void drawBar(MatrixStack matrix, int mX, int mY, TextRenderer textRend) {
        /* background and title bar */
        fillGrey(matrix, x1, y1, x2, y2);
        fillGradient(matrix, x1 + 2, y1 + 2, x2 - 2, y1 + 12, (selected ? 0xff0000ff : 0xff606060), (selected ? 0xff4080ff : 0xffa0a0a0));

        /* buttons */
        fillGrey(matrix, x2 - 12, y1 + 3, x2 - 4, y1 + 11);
        textRend.draw(matrix, "x", x2 - 11, y1 + 2, 0x000000);

        fillGrey(matrix, x2 - 22, y1 + 3, x2 - 14, y1 + 11);
        textRend.draw(matrix, "_", x2 - 21, y1 + 1, 0x000000);
    }

    public boolean shouldClose(int mX, int mY) {
        return selected && mX > x2 - 23 && mX < x2 && mY > y1 + 2 && mY < y1 + 12;
    }

    public void onMousePressed(int x, int y) {
        if (inactiveTime > 0) {
            return;
        }

        if (x > x1 + 2 && x < x2 - 2 && y > y1 + 2 && y < y1 + 12) {
            dragging = true;
            dragOffX = x - x1;
            dragOffY = y - y1;
        }

        for (WindowButton w : buttons) {
            if (x >= x1 + w.x1 && x <= x1 + w.x2 && y >= y1 + w.y1 && y <= y1 + w.y2) {
                w.action.run();
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
        }
    }

    public void onMouseReleased(int x, int y) {
        dragging = false;
    }

    public void fillGrey(MatrixStack matrix, int x1, int y1, int x2, int y2) {
        DrawableHelper.fill(matrix, x1, y1, x2 - 1, y2 - 1, 0xffb0b0b0);
        DrawableHelper.fill(matrix, x1 + 1, y1 + 1, x2, y2, 0xff000000);
        DrawableHelper.fill(matrix, x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0xff858585);
    }

    public static void fillGradient(MatrixStack matrix, int x1, int y1, int x2, int y2, int color1, int color2) {
        float alpha1 = (color1 >> 24 & 255) / 255.0F;
        float red1   = (color1 >> 16 & 255) / 255.0F;
        float green1 = (color1 >> 8 & 255) / 255.0F;
        float blue1  = (color1 & 255) / 255.0F;
        float alpha2 = (color2 >> 24 & 255) / 255.0F;
        float red2   = (color2 >> 16 & 255) / 255.0F;
        float green2 = (color2 >> 8 & 255) / 255.0F;
        float blue2  = (color2 & 255) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x1, y1, 0).color(red1, green1, blue1, alpha1).next();
        bufferBuilder.vertex(x1, y2, 0).color(red1, green1, blue1, alpha1).next();
        bufferBuilder.vertex(x2, y2, 0).color(red2, green2, blue2, alpha2).next();
        bufferBuilder.vertex(x2, y1, 0).color(red2, green2, blue2, alpha2).next();
        tessellator.draw();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
    }
}
