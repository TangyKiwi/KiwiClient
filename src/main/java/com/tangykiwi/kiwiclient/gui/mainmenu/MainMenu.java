package com.tangykiwi.kiwiclient.gui.mainmenu;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class MainMenu extends Screen {

    public final String[] BUTTONS = {"Singleplayer", "Multiplayer", "Realms", "Options", "Language", "Quit"};
    public final ArrayList<GuiButton> buttonList = new ArrayList<GuiButton>();

    public MainMenu() {
        super(new TranslatableText("narrator.screen.title"));
    }

    public void init() {
        buttonList.clear();
        int initHeight = this.height / 2;
        int objHeight = 50;
        int objWidth = 50;
        int xMid = this.width / 2;

        buttonList.add(new GuiButton(0, xMid - 150, initHeight, objWidth, objHeight, BUTTONS[0]));
        buttonList.add(new GuiButton(1, xMid - 90, initHeight, objWidth, objHeight, BUTTONS[1]));
        buttonList.add(new GuiButton(2, xMid - 30, initHeight, objWidth, objHeight, BUTTONS[2]));
        buttonList.add(new GuiButton(3, xMid + 30, initHeight, objWidth, objHeight, BUTTONS[3]));
        buttonList.add(new GuiButton(4, xMid + 90, initHeight, objWidth, objHeight, BUTTONS[4]));
        buttonList.add(new GuiButton(5, xMid + 150, initHeight, objWidth, objHeight, BUTTONS[5]));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);

        RenderSystem.setShaderTexture(0, KiwiClient.MENU);
        this.drawTexture(matrixStack, 0, 0, 20 * mouseX / this.width,  20 * mouseY / this.height, this.width + 20 * mouseX / this.width, this.height + 20 * mouseY / this.height, this.width + 40, this.height + 40);
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, 0x00000000, 0xff000000);

        GlStateManager._enableBlend();
        RenderSystem.setShaderTexture(0, new Identifier("kiwiclient:textures/menu/title.png"));
        this.drawTexture(matrixStack, this.width / 2 - 160, 3 * this.height / 10, 0, 0, 320, 40, 320, 40);
        GlStateManager._disableBlend();

        for(GuiButton b : buttonList) {
            b.drawButton(matrixStack, mouseX, mouseY);
        }

        super.render(matrixStack, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(int i = 0; i < 6; i++) {
            String b = BUTTONS[i];
            GuiButton guiButton = buttonList.get(i);
            float x = guiButton.x;
            float y = guiButton.y;

            if(mouseX >= x - guiButton.width / 2 && mouseY >= y && mouseX <= x + guiButton.width / 2 && mouseY <= y + 60) {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                switch(b) {
                    case "Singleplayer":
                        this.client.setScreen(new SelectWorldScreen(this));
                        break;
                    case "Multiplayer":
                        Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
                        this.client.setScreen(screen);
                        break;
                    case "Realms":
                        this.client.setScreen(new RealmsMainScreen(this));
                        break;
                    case "Options":
                        this.client.setScreen(new OptionsScreen(this, this.client.options));
                        break;
                    case "Language":
                        this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
                        break;
                    case "Quit":
                        this.client.scheduleStop();
                        break;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}