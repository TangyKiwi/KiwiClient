package com.tangykiwi.kiwiclient.gui;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.modules.Category;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.util.ColorUtil;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashSet;
import java.util.Set;

public class ClickGuiScreen extends AbstractWindowScreen {

    private int keyDown = -1;
    private boolean lmDown = false;
    private boolean rmDown = false;
    private boolean lmHeld = false;
    private int mwScroll = 0;

    private TextFieldWidget searchField;

    public ClickGuiScreen() {
        super(new LiteralText("ClickGui"));
    }

    public void init() {
        searchField = new TextFieldWidget(textRenderer, 2, 14, 100, 12, LiteralText.EMPTY /* @LasnikProgram is author lol*/);
        searchField.visible = false;
        searchField.setMaxLength(20);
        searchField.setSuggestion("Search here");
        addButton(searchField);
    }

    public void initWindows() {
        int len = 85;

        int i = 10;
        windows.add(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.PLAYER), i, 35, len,
                StringUtils.capitalize(StringUtils.lowerCase(Category.PLAYER.toString())), new ItemStack(Items.PLAYER_HEAD)));
        i += len + 5;
        windows.add(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.COMBAT), i, 35, len,
                StringUtils.capitalize(StringUtils.lowerCase(Category.COMBAT.toString())), new ItemStack(Items.NETHERITE_SWORD)));
        i += len + 5;
        windows.add(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.RENDER), i, 35, len,
                StringUtils.capitalize(StringUtils.lowerCase(Category.RENDER.toString())), new ItemStack(Items.ENDER_EYE)));
        i += len + 5;
        windows.add(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.MOVEMENT), i, 35, len,
                StringUtils.capitalize(StringUtils.lowerCase(Category.MOVEMENT.toString())), new ItemStack(Items.DIAMOND_BOOTS)));
        i += len + 5;
        windows.add(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.CLIENT), i, 35, len,
                StringUtils.capitalize(StringUtils.lowerCase(Category.CLIENT.toString())), new ItemStack(Items.GLASS_PANE)));
        /**
        i += len + 5;
        windows.add(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.WORLD), i, 35, len,
                StringUtils.capitalize(StringUtils.lowerCase(Category.WORLD.toString())), new ItemStack(Items.GRASS_BLOCK)));
        i += len + 5;
        windows.add(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.EXPLOITS), i, 35, len,
                StringUtils.capitalize(StringUtils.lowerCase(Category.EXPLOITS.toString())), new ItemStack(Items.BEDROCK)));
        i += len + 5;
        windows.add(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.CHAT), i, 35, len,
                StringUtils.capitalize(StringUtils.lowerCase(Category.CHAT.toString())), new ItemStack(Items.WRITABLE_BOOK)));
        i += len + 5;
        windows.add(new ModuleWindow(KiwiClient.moduleManager.getModulesInCat(Category.MISC), i, 35, len,
                StringUtils.capitalize(StringUtils.lowerCase(Category.MISC.toString())), new ItemStack(Items.SPAWNER)));
        */
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void onClose() {
        KiwiClient.moduleManager.getModule(ClickGui.class).setToggled(false);
        client.openScreen(null);
    }

    public void render(MatrixStack matrix, int mX, int mY, float float_1) {
        searchField.visible = true;

        this.renderBackground(matrix);
        searchField.setSuggestion(searchField.getText().isEmpty() ? "Search here" : "");

        Set<Module> seachMods = new HashSet<>();
        if (!searchField.getText().isEmpty()) {
            for (Module m : KiwiClient.moduleManager.moduleList) {
                if (m.getName().toLowerCase().contains(searchField.getText().toLowerCase().replace(" ", ""))) {
                    seachMods.add(m);
                }
            }
        }

        for (Window w : windows) {
            if (w instanceof ModuleWindow) {
                ((ModuleWindow) w).setSearchedModule(seachMods);
            }
        }

        int len = 85;

        for (Window w : windows) {
            if (w instanceof ClickGuiWindow) {
                if (w instanceof ModuleWindow) {
                    ((ModuleWindow) w).setLen(len);
                }

                ((ClickGuiWindow) w).updateKeys(mX, mY, keyDown, lmDown, rmDown, lmHeld, mwScroll);
            }
        }

        super.render(matrix, mX, mY, float_1);


        for (Window w : windows) {
            if (w instanceof ClickGuiWindow) {
                Triple<Integer, Integer, String> tooltip = ((ClickGuiWindow) w).getTooltip();
                if (tooltip != null) {
                    textRenderer.drawWithShadow(matrix, tooltip.getRight(), 2, height - 30, ColorUtil.guiColour());
                }
            }
        }

        lmDown = false;
        rmDown = false;
        keyDown = -1;
        mwScroll = 0;
    }

    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        if (int_1 == 0) {
            lmDown = true;
            lmHeld = true;
        } else if (int_1 == 1)
            rmDown = true;

        // Fix having to double click windows to move them
        for (Window w : windows) {
            if (double_1 > w.x1 && double_1 < w.x2 && double_2 > w.y1 && double_2 < w.y2 && !w.closed) {
                w.onMousePressed((int) double_1, (int) double_2);
                break;
            }
        }

        return super.mouseClicked(double_1, double_2, int_1);
    }

    public boolean mouseReleased(double double_1, double double_2, int int_1) {
        if (int_1 == 0)
            lmHeld = false;
        return super.mouseReleased(double_1, double_2, int_1);
    }

    public boolean keyPressed(int int_1, int int_2, int int_3) {
        keyDown = int_1;
        return super.keyPressed(int_1, int_2, int_3);
    }
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        mwScroll = (int) double_3;
        return super.mouseScrolled(double_1, double_2, double_3);
    }
    public void resetGui() {
        int x = 30;
        for (Window m : windows) {
            m.x1 = x;
            m.y2 = 35;
            x += 90;
        }
    }
}
