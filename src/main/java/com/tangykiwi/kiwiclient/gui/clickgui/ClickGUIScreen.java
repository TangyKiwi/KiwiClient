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
    public static ClickGUIScreen INSTANCE = new ClickGUIScreen();

    List<CategoryWindow> windows = new ArrayList<CategoryWindow>();

    protected int keyDown = -1;
    protected boolean lmDown = false;
    protected boolean rmDown = false;
    protected boolean lmHeld = false;
    protected int mwhScroll = 0;
    protected int mwvScroll = 0;

    public ClickGUIScreen() {
        super(Text.literal("ClickGUI"));
    }

    public void initWindows() {

        int i = 10;
        windows.add(new CategoryWindow(i, 18, 85, Category.PLAYER, new ItemStack(Items.PLAYER_HEAD)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, Category.COMBAT, new ItemStack(Items.DIAMOND_SWORD)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, Category.RENDER, new ItemStack(Items.ENDER_EYE)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, Category.MOVEMENT, new ItemStack(Items.DIAMOND_BOOTS)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, Category.CLIENT, new ItemStack(Items.GLASS_PANE)));
        i += 90;
        windows.add(new CategoryWindow(i, 18, 85, Category.OTHER, new ItemStack(Items.COMMAND_BLOCK)));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        for (CategoryWindow window : windows) {
            window.updateKeys(mouseX, mouseY, keyDown, lmDown, rmDown, lmHeld, mwvScroll);
        }

        super.render(context, mouseX, mouseY, delta);

        for (CategoryWindow window : windows) {
            window.render(context, mouseX, mouseY);
        }

        lmDown = false;
        rmDown = false;
        keyDown = -1;
        mwhScroll = 0;
        mwvScroll = 0;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            lmDown = true;
            lmHeld = true;
        } else if (button == 1) {
            rmDown = true;
        }

        for (CategoryWindow window : windows) {
            if (mouseX > window.x && mouseX < window.x + window.width && mouseY > window.y && mouseY < window.y + window.height) {
                window.mouseClicked(mouseX, mouseY, button);
                break;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) lmHeld = false;

        for (CategoryWindow window : windows) {
            window.mouseReleased(mouseX, mouseY, button);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        keyDown = keyCode;

        for (CategoryWindow window : windows) {
            window.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        mwhScroll = (int) horizontalAmount;
        mwvScroll = (int) verticalAmount;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
