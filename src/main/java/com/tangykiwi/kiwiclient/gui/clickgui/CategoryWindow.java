package com.tangykiwi.kiwiclient.gui.clickgui;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
import com.tangykiwi.kiwiclient.module.client.ClickGUI;
import com.tangykiwi.kiwiclient.util.RenderUtils;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryWindow {
    public List<Module> moduleList = new ArrayList<Module>();

    private int x, y;
    private int width, height;
    private Category category;
    private String title;
    private ItemStack icon;
    private boolean expanded = true;

    private FontRenderer fontRenderer;
    private float fontHeight;

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
        int trueLen = (int) (expanded ? y + fontHeight + 1 /*+ getHeight()*/ : y + fontHeight + 1);

        MatrixStack matrixStack = context.getMatrices();

        /* background */
        RenderUtils.drawRoundedQuadWH(matrixStack, new Color(0xffb08760), x, y, width, height, 5, 90);

        /* expansion background */
        if(expanded) {
            RenderUtils.drawRoundedQuadXY(matrixStack, new Color(0x90907760), x + 1, y + fontHeight + 2, x + width - 1, y + height - 1, 5, 90);
        }

        /* base title */
        RenderUtils.drawRoundedQuadWH(matrixStack, new Color(0xffb09070), x + 1, y + 1, width - 2, fontHeight + 1, 5, 90);

        fontRenderer.drawStringWithShadow(matrixStack, expanded ? "-" : "+", x + width - 10, y + (expanded ? 2 : 4), -1);

        boolean blockItem = icon != null && icon.getItem() instanceof BlockItem;

        /* window icon */
//        if (icon != null) {
//            matrixStack.push();
//            matrixStack.translate(x + (blockItem ? 3 : 2), y + 1, 0);
//            matrixStack.scale(0.6f, 0.6f, 1f);
//
//            DiffuseLighting.enableGuiDepthLighting();
//            context.drawItem(icon, 0, 0);
//            DiffuseLighting.disableGuiDepthLighting();
//
//            matrixStack.pop();
//        }

        /* window title */
        fontRenderer.drawStringWithShadow(matrixStack, title, x + (icon == null || icon.getItem() == Items.AIR ? 4 : (blockItem ? 15 : 14)), y + 3, -1);
    }
}