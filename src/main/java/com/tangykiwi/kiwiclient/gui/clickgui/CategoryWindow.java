package com.tangykiwi.kiwiclient.gui.clickgui;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.client.ClickGUI;
import com.tangykiwi.kiwiclient.module.setting.Setting;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import static com.tangykiwi.kiwiclient.KiwiClient.mc;

public class CategoryWindow {
    // List of Modules in cat : expanded in clickgui
    public LinkedHashMap<Module, Boolean> modList = new LinkedHashMap<>();

    public int x, y;
    public int width, height;
    private Category category;
    private String title;
    private ItemStack icon;
    private boolean expanded = true;

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

        for (Module m : KiwiClient.moduleManager.getModulesInCat(this.category)) {
            this.modList.put(m, false);
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        if (dragging) {
            x = Math.max(0, mouseX - dragOffX);
            y = Math.max(0, mouseY - dragOffY);
        }

        if (rmDown && mouseOver(x, y, x + width, y + (int) fontHeight)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            expanded = !expanded;
        }

        int trueHeight = (int) fontHeight + 4;
        if (expanded) trueHeight += Math.min((fontHeight + 1) * height, (fontHeight + 1) * modList.size()) + 2;

        /* background */
        // RenderUtils.drawRectWH(context, x, y, width, height, 0xfff7a558);
        RenderUtils.drawRoundedQuadWH(context, x, y, width, trueHeight, 5, 90, 0xfff7a558);

        /* expansion background */
        if(expanded) {
            // RenderUtils.drawRectXY(context, x + 1, y + fontHeight + 2, x + width - 1, y + height - 1, 0x90d9904b);
            RenderUtils.drawRoundedQuadXY(context, x + 1, y + fontHeight + 2, x + width - 1, y + trueHeight - 1, 5, 90, 0x90d9904b);
        }

        /* base title */
        // RenderUtils.drawRectWH(context, x + 1, y + 1, width - 2, fontHeight + 1, 0xffec8625);
        RenderUtils.drawRoundedQuadWH(context, x + 1, y + 1, width - 2, fontHeight + 1, 5, 90, 0xffec8625);

        fontRenderer.drawStringWithShadow(context, expanded ? "-" : "+", x + width - 10, y + 2, -1);

        boolean blockItem = icon != null && icon.getItem() instanceof BlockItem;

        /* window icon */
        if (icon != null) {
            RenderUtils.drawItem(context, icon, x + (blockItem ? 3 : 2), y + 1, 0.6f);
        }

        /* window title */
        fontRenderer.drawStringWithShadow(context, title, x + (icon == null || icon.getItem() == Items.AIR ? 4 : (blockItem ? 15 : 14)), y + 3, -1);

        if (expanded) {
            int curYoffset = (int) fontHeight + 4;
            for (Entry<Module, Boolean> entry : modList.entrySet()) {
                Module module = entry.getKey();
                Boolean showSettings = entry.getValue();

                if (mouseOver(x, y + curYoffset, x + width, y + curYoffset + (int) fontHeight + 1)) {
                    RenderUtils.drawRoundedQuadWH(context, x + 1, y + curYoffset + 1, width - 2, (int) fontHeight + 1, 5, 90, 0x70303070);

                    if (lmDown) {
                        module.toggle();
                    }
                    if (rmDown) {
                        modList.replace(module, !showSettings);
                        showSettings = !showSettings;
                    }
                    if (lmDown || rmDown) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    }
                }

                fontRenderer.drawStringWithShadow(context, module.getName(), x + 4, y + 2 + curYoffset, module.isEnabled() ? 0x70efe0 : 0xc0c0c0);

                String color = showSettings ? "\u00a7a" : "\u00a7c";

                if (showSettings) {
                    fontRenderer.drawString(context, color + "v", x + width - 8, y + 2 + curYoffset, -1);
                    curYoffset += fontHeight + 1;

                    for (Setting<?> s : module.getSettings()) {
                        int additionalOffset = s.render(context, this, curYoffset);
                        curYoffset += additionalOffset;
                        trueHeight += additionalOffset;
                    }
                } else {
                    fontRenderer.drawString(context, color + "\u00a7l>", x + width - 8, y + 2 + curYoffset, -1);
                    curYoffset += fontHeight + 1;
                }
            }
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