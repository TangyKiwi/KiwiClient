package com.tangykiwi.kiwiclient.gui.hudeditor.components;

import com.mojang.blaze3d.platform.InputConstants;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.hudeditor.HUDEditorScreen;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;

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

    public boolean xCollide, yCollide;
    public boolean prev_xCollide, prev_yCollide;

    public int minX, minY, maxX, maxY;

    public HUDComponent(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.fontRenderer = KiwiClient.fontManager.getSize(6, FontManager.Type.CONSOLAS);
        this.fontHeight = fontRenderer.getStringHeight(name);
        this.height = (int) this.fontHeight;
    }

    public void render(GuiGraphicsExtractor context) {
        if (dragging) {
            if (InputConstants.isKeyDown(KiwiClient.mc.getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
                if (x < KiwiClient.mc.gui.screen().width / 2) {
                    minX = 0;
                    maxX = KiwiClient.mc.gui.screen().width / 2;
                } else {
                    minX = KiwiClient.mc.gui.screen().width / 2;
                    maxX = KiwiClient.mc.gui.screen().width;
                }
                if (y < KiwiClient.mc.gui.screen().height / 2) {
                    minY = 0;
                    maxY = KiwiClient.mc.gui.screen().height / 2;
                } else {
                    minY = KiwiClient.mc.gui.screen().height / 2;
                    maxY = KiwiClient.mc.gui.screen().height;
                }

                float newX = mouseX - dragOffX;
                float newY = mouseY - dragOffY;

                boolean collision = false;

                for (HUDComponent component : HUDEditorScreen.INSTANCE.components) {
                    if (component.getName() != this.name) {
                        checkCollision(newX, newY, component);
                        if (xCollide && yCollide) {
                            float xC1 = x + width * 0.5F;
                            float yC1 = y + height * 0.5F;
                            float xC2 = component.getX() + component.getWidth() * 0.5F;
                            float yC2 = component.getY() + component.getHeight() * 0.5F;
                            if (!prev_xCollide && !prev_yCollide) {
                                if ((width + component.getWidth()) * 0.5F - Math.abs(xC1 - xC2) >
                                (height + component.getHeight()) * 0.5F - Math.abs(yC1 - yC2)) {
                                    prev_xCollide = true;
                                } else {
                                    prev_yCollide = true;
                                }
                            }
                            if (prev_xCollide) {
                                renderBoundingBox(context);
                                component.renderBoundingBox(context);
                                if (yC1 < yC2) {
                                    x = Math.min(Math.max(minX, mouseX - dragOffX), maxX - width);
                                    y = component.getY() - height;
                                } else {
                                    x = Math.min(Math.max(minX, mouseX - dragOffX), maxX - width);
                                    y = component.getY() + component.getHeight();
                                }
                            } else if (prev_yCollide) {
                                renderBoundingBox(context);
                                component.renderBoundingBox(context);
                                if (xC1 < xC2) {
                                    x = component.getX() - width;
                                    y = Math.min(Math.max(minY, mouseY - dragOffY), maxY - height);
                                } else {
                                    x = component.getX() + component.getWidth();
                                    y = Math.min(Math.max(minY, mouseY - dragOffY), maxY - height);
                                }
                            }
                            collision = prev_xCollide || prev_yCollide;
                        }
                    }
                }
                if(!collision) {
                    x = Math.min(Math.max(minX, mouseX - dragOffX), maxX - width);
                    y = Math.min(Math.max(minY, mouseY - dragOffY), maxY - height);
                }
            } else {
                x = Math.min(Math.max(0, mouseX - dragOffX), KiwiClient.mc.gui.screen().width - width);
                y = Math.min(Math.max(0, mouseY - dragOffY), KiwiClient.mc.gui.screen().height - height);
            }
        }

        if (mouseOver((int) x, (int) y, (int) x + width, (int) y + height)) {
            renderBoundingBox(context);
        }
    }

    public void checkCollision(float newX, float newY, HUDComponent component) {
        if (!xCollide || !yCollide) {
            prev_xCollide = xCollide;
            prev_yCollide = yCollide;
        }

        if (newX < component.getX() + component.getWidth() &&
            newX + width > component.getX()) xCollide = true;
        else xCollide = false;

        if (newY < component.getY() + component.getHeight() &&
            newY + height > component.getY()) yCollide = true;
        else yCollide = false;

        if (xCollide != yCollide) {
            xCollide = false;
            yCollide = false;
        }
    }

    public void renderBoundingBox(GuiGraphicsExtractor context) {
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