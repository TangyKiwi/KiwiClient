package com.tangykiwi.kiwiclient.gui.clickgui;

import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.Base;
import com.tangykiwi.kiwiclient.gui.hudeditor.HUDEditorScreen;
import com.tangykiwi.kiwiclient.module.Category;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends Base {
    public static ClickGUIScreen INSTANCE = new ClickGUIScreen();
    public FontRenderer fontRenderer;

    public List<CategoryWindow> windows = new ArrayList<CategoryWindow>();

    protected int keyDown = -1;
    protected boolean lmDown = false;
    protected boolean rmDown = false;
    protected boolean lmHeld = false;
    protected int mwhScroll = 0;
    protected int mwvScroll = 0;

    public ClickGUIScreen() {
        super(Text.literal("ClickGUI"));
        fontRenderer = KiwiClient.fontManager.getSize(8, FontManager.Type.CONSOLAS);
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

        context.fill(width / 2 - 50, -1, width / 2 - 2, 12, 
            mouseX >= width / 2 - 50 && mouseX <= width / 2 - 2 && mouseY >= 0 && mouseY <= 12 ? 0x60b070f0 : 0x60606090);
        context.fill(width / 2 + 2, -1, width / 2 + 50, 12,
            mouseX >= width / 2 + 2 && mouseX <= width / 2 + 50 && mouseY >= 0 && mouseY <= 12 ? 0x60b070f0 : 0x60606090);
        fontRenderer.drawCenteredStringWithShadow(context, "ClickGUI", width / 2 - 26, 2, 0xf0f0f0);
        fontRenderer.drawCenteredStringWithShadow(context, "HUD Editor", width / 2 + 26, 2, 0xf0f0f0);

        lmDown = false;
        rmDown = false;
        keyDown = -1;
        mwhScroll = 0;
        mwvScroll = 0;
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();
        if (button == 0) {
            if (mouseX >= width / 2 - 50 && mouseX <= width / 2 - 2 && mouseY >= 0 && mouseY <= 12) {
				this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
				this.client.setScreen(INSTANCE);
			} else if (mouseX >= width / 2 + 2 && mouseX <= width / 2 + 50 && mouseY >= 0 && mouseY <= 12) {
				this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1f));
                this.client.setScreen(HUDEditorScreen.INSTANCE);
			} else {
                lmDown = true;
                lmHeld = true;
            }
        } else if (button == 1) {
            rmDown = true;
        }

        for (CategoryWindow window : windows) {
            if (mouseX > window.x && mouseX < window.x + window.width && mouseY > window.y && mouseY < window.y + window.height) {
                window.mouseClicked(mouseX, mouseY, button);
                break;
            }
        }

        return super.mouseClicked(click, doubled);
    }

    public boolean mouseReleased(Click click) {
        if (click.button() == 0) lmHeld = false;

        for (CategoryWindow window : windows) {
            window.mouseReleased(click.x(), click.y(), click.button());
        }

        return super.mouseReleased(click);
    }

    public boolean keyPressed(KeyInput keyInput) {
        int keyCode = keyInput.getKeycode();
        int scanCode = keyInput.scancode();
        int modifiers = keyInput.modifiers();
        keyDown = keyCode;

        for (CategoryWindow window : windows) {
            window.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyInput);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        mwhScroll = (int) horizontalAmount;
        mwvScroll = (int) verticalAmount;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public CategoryWindow getWindow(String windowName) {
        for (CategoryWindow w : windows) {
            if (w.title.equals(windowName)) return w;
        }
        return null;
    }
}
