package com.tangykiwi.kiwiclient.gui.clickgui;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.module.Module;
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

    public CategoryWindow(int x, int y, int width, int height, Category category, ItemStack icon) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = category;
        this.title = StringUtils.capitalize(StringUtils.lowerCase(this.category.toString()));
        this.icon = icon;

        this.fontRenderer = KiwiClient.fontManager.getSize(8, FontManager.Type.CONSOLAS);

        moduleList = KiwiClient.moduleManager.getModulesInCat(this.category);
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        MatrixStack matrixStack = context.getMatrices();

        RenderUtils.drawRoundedQuadWH(matrixStack, new Color(0xffb08760), x, y, width, height, 5, 20);

        if(!expanded) {
            RenderUtils.drawRoundedQuadWH(matrixStack, new Color(0x90907760), x + 1, y + 1, width - 1, height - 1, 5, 20);
        }

        RenderUtils.drawRoundedQuadWH(matrixStack, new Color(0xffb09070), x + 1, y + 1, width - 1, 12, 5, 20);

        fontRenderer.drawStringWithShadow(matrixStack, expanded ? "-" : "+", x + width - 10, y, -1);

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