package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.hudeditor.HUDEditorScreen;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;

import java.awt.Color;

import org.lwjgl.glfw.GLFW;

public abstract class HUDComponent {
    private String name;
    private float x, y;
    private int width, height;

    public FontRenderer fontRenderer;
    public float fontHeight;

    protected boolean dragging;
    protected int dragOffX;
    protected int dragOffY;

    public int mouseX;
    public int mouseY;

    public int keyDown = -1;
    public boolean lmDown = false;
    public boolean rmDown = false;
    public boolean lmHeld = false;
    public int mwScroll = 0;

    public HUDComponent(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.fontRenderer = KiwiClient.fontManager.getSize(6, FontManager.Type.CONSOLAS);
        this.fontHeight = fontRenderer.getStringHeight(name);
        this.height = (int) this.fontHeight;
    }

    public void render(DrawContext context) {
        if (dragging) {
            if (InputUtil.isKeyPressed(KiwiClient.mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                // float newX = mouseX - dragOffX;
                // float newY = mouseY - dragOffY;

                // for (HUDComponent component : HUDEditorScreen.INSTANCE.components) {
                //     if (component.getName() != this.name) {
                //         float compX = component.getX();
                //         float compY = component.getY();
                //         int compW = component.getWidth();
                //         int compH = component.getHeight();

                //         // left collision
                //         if (newX + width > compX && newX + width < compX + compW) {
                //             // top || bottom bounds
                //             if ((compY < newY && newY < compY + compH) || (compY < newY + height && newY + height < compY + compH)) {
                //                 component.renderBoundingBox(context);
                //                 y = Math.min(Math.max(0, mouseY - dragOffY), KiwiClient.mc.currentScreen.height - height);
                //             } else {
                //                 component.renderBoundingBox(context);
                //                 x = Math.min(Math.max(0, mouseX - dragOffX), KiwiClient.mc.currentScreen.width - width);
                //             }
                //         // right collision
                //         } else if (newX < compX + compW && newX + width > compX) {
                //             // top || bottom bounds
                //             if ((compY < newY && newY < compY + compH) || (compY < newY + height && newY + height < compY + compH)) {
                //                 component.renderBoundingBox(context);
                //                 y = Math.min(Math.max(0, mouseY - dragOffY), KiwiClient.mc.currentScreen.height - height);
                //             } else {
                //                 component.renderBoundingBox(context);
                //                 x = Math.min(Math.max(0, mouseX - dragOffX), KiwiClient.mc.currentScreen.width - width);
                //             }
                //         }
                //     }
                // }
            } else {
                x = Math.min(Math.max(0, mouseX - dragOffX), KiwiClient.mc.currentScreen.width - width);
                y = Math.min(Math.max(0, mouseY - dragOffY), KiwiClient.mc.currentScreen.height - height);
            }
        }

        if (mouseOver((int) x, (int) y, (int) x + width, (int) y + height)) {
            renderBoundingBox(context);
        }
    }

    public void renderBoundingBox(DrawContext context) {
        RenderUtils.drawRectWH(context, x, y, width, height, 0x60000000);
    }

    public String getName() {
        return name;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getColorString(int value, int best, int good, int mid, int bad, int worst, Boolean reverse) {
        Color color = Color.GRAY; // default
        if (!reverse ? value > best : value < best) {color = Color.GREEN;}
        else if (!reverse ? value > good : value < good) {color = Color.YELLOW;}
        else if (!reverse ? value > mid : value < mid) {color = new Color(255, 191, 0);}
        else if (!reverse ? value > bad : value < bad) {color = new Color(255, 191, 0);}
        else if (!reverse ? value > worst : value < worst) {color = Color.ORANGE;}
        else {color = Color.RED;}
        return (int) Long.parseLong(Integer.toHexString(color.getRGB()), 16);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= x && mouseX <= x + width - 2 && mouseY >= y && mouseY <= y + height) {
            dragging = true;
            dragOffX = (int) (mouseX - x);
            dragOffY = (int) (mouseY - y);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        keyDown = keyCode;
    }

    public boolean mouseOver(int minX, int minY, int maxX, int maxY) {
        return mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY < maxY;
    }

    public void updateKeys(int mouseX, int mouseY, int keyDown, boolean lmDown, boolean rmDown, boolean lmHeld, int mwScroll) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.keyDown = keyDown;
        this.lmDown = lmDown;
        this.rmDown = rmDown;
        this.lmHeld = lmHeld;
        this.mwScroll = mwScroll;
    }
}