package com.tangykiwi.kiwiclient.gui.clickgui;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.client.ClickGUI;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class CategoryWindow {
    public List<Module> moduleList = new ArrayList<Module>();

    public int x, y;
    public int width, height;
    private Category category;
    private String title;
    private ItemStack icon;
    private boolean expanded = true;

    private FontRenderer fontRenderer;
    private float fontHeight;

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

    public CategoryWindow(int x, int y, int width, Category category, ItemStack icon) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = ((ClickGUI) KiwiClient.moduleManager.getModule("ClickGUI")).length.getValueInt();
        this.category = category;
        this.title = StringUtils.capitalize(StringUtils.lowerCase(this.category.toString()));
        this.icon = icon;

        this.fontRenderer = KiwiClient.fontManager.getSize(8, FontManager.Type.CONSOLAS);
        this.fontHeight = fontRenderer.getStringHeight(title);

        moduleList = KiwiClient.moduleManager.getModulesInCat(this.category);
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        if (dragging) {
            x = Math.max(0, mouseX - dragOffX);
            y = Math.max(0, mouseY - dragOffY);
        }

        int trueLen = (int) (expanded ? y + fontHeight + 1 /*+ getHeight()*/ : y + fontHeight + 1);

        Matrix3x2fStack matrixStack = context.getMatrices();

        /* background */
        RenderUtils.drawRectWH(context, x, y, width, height, 0xfff7a558);
        // RenderUtils.drawRoundedQuadWH(context, new Color(0xfff7a558), x, y, width, height, 5, 90);

        /* expansion background */
        if(expanded) {
            RenderUtils.drawRectXY(context, x + 1, y + fontHeight + 2, x + width - 1, y + height - 1, 0x90d9904b);
            // RenderUtils.drawRoundedQuadXY(context, new Color(0x90d9904b), x + 1, y + fontHeight + 2, x + width - 1, y + height - 1, 5, 90);
        }

        /* base title */
        RenderUtils.drawRectWH(context, x + 1, y + 1, width - 2, fontHeight + 1, 0xffec8625);
        // RenderUtils.drawRoundedQuadWH(context, new Color(0xffec8625), x + 1, y + 1, width - 2, fontHeight + 1, 5, 90);

        fontRenderer.drawStringWithShadow(context, expanded ? "-" : "+", x + width - 10, y + 2, -1);

        boolean blockItem = icon != null && icon.getItem() instanceof BlockItem;

        /* window icon */
        if (icon != null) {
            RenderUtils.drawItem(context, icon, x + (blockItem ? 3 : 2), y + 1, 0.6f);
        }

        /* window title */
        fontRenderer.drawStringWithShadow(context, title, x + (icon == null || icon.getItem() == Items.AIR ? 4 : (blockItem ? 15 : 14)), y + 3, -1);

        if (rmDown && mouseOver(x, y, x + width, y + 13)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            expanded = !expanded;
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= x && mouseX <= x + width - 2 && mouseY >= y && mouseY <= y + height + 11) {
            dragging = true;
            dragOffX = (int) mouseX - x;
            dragOffY = (int) mouseY - y;
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {

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