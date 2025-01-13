package com.tangykiwi.kiwiclient.gui.clickgui;

import com.tangykiwi.kiwiclient.gui.Base;
import com.tangykiwi.kiwiclient.module.Category;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends Base {
    List<CategoryWindow> windows = new ArrayList<CategoryWindow>();

    public ClickGUIScreen() {
        super(Text.literal("ClickGUI"));

        int i = 10;
        windows.add(new CategoryWindow(i, 18, 85, 30, Category.PLAYER, new ItemStack(Items.PLAYER_HEAD)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, 30, Category.COMBAT, new ItemStack(Items.DIAMOND_SWORD)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, 30, Category.RENDER, new ItemStack(Items.ENDER_EYE)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, 30, Category.MOVEMENT, new ItemStack(Items.DIAMOND_BOOTS)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, 30, Category.CLIENT, new ItemStack(Items.GLASS_PANE)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, 30, Category.OTHER, new ItemStack(Items.COMMAND_BLOCK)));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        for (CategoryWindow window : windows) {
            window.render(context, mouseX, mouseY);
        }
    }
}
