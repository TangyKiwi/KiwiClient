package com.tangykiwi.kiwiclient.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.util.Textures;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;

public class MainMenu extends Screen {
    public final String[] BUTTONS = {"Singleplayer", "Multiplayer", "Realms", "Options", "Language", "Quit"};
    public final ArrayList<GuiButton> buttonList = new ArrayList<GuiButton>();
    public static FontRenderer fontRenderer;

    public MainMenu() {
        super(Text.translatable("narrator.screen.title"));
    }

    public void init() {
        buttonList.clear();
        int initHeight = this.height / 2;
        int objHeight = 50;
        int objWidth = 50;
        int xMid = this.width / 2;

        fontRenderer = KiwiClient.fontManager.getSize(8, FontManager.Type.CONSOLAS);
        buttonList.add(new GuiButton(0, xMid - 150, initHeight, objWidth, objHeight, BUTTONS[0], fontRenderer));
        buttonList.add(new GuiButton(1, xMid - 90, initHeight, objWidth, objHeight, BUTTONS[1], fontRenderer));
        buttonList.add(new GuiButton(2, xMid - 30, initHeight, objWidth, objHeight, BUTTONS[2], fontRenderer));
        buttonList.add(new GuiButton(3, xMid + 30, initHeight, objWidth, objHeight, BUTTONS[3], fontRenderer));
        buttonList.add(new GuiButton(4, xMid + 90, initHeight, objWidth, objHeight, BUTTONS[4], fontRenderer));
        buttonList.add(new GuiButton(5, xMid + 150, initHeight, objWidth, objHeight, BUTTONS[5], fontRenderer));
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier menubg = Textures.MENU;
        context.drawTexture(RenderLayer::getGuiTextured, menubg, 0, 0, 20 * mouseX / this.width,  20 * mouseY / this.height, this.width + 20 * mouseX / this.width, this.height + 20 * mouseY / this.height, this.width + 40, this.height + 40);
        context.fillGradient(0, 0, this.width, this.height, 0x00000000, 0xff000000);

        String version = "v" + KiwiClient.VERSION + " - MC " + KiwiClient.MC_VERSION;
        context.fill(0, 0, (int) fontRenderer.getStringWidth(version) + 4, (int) fontRenderer.getStringHeight(version) + 2, 0x90000000);
        fontRenderer.drawString(context.getMatrices(), version, 1, 2, new Color(-1));

        RenderSystem.enableBlend();
        context.drawTexture(RenderLayer::getGuiTextured, Textures.TITLE, this.width / 2 - 160, this.height / 2 - 55, 0, 0, 320, 40, 320, 40);
        RenderSystem.disableBlend();

        for(GuiButton b : buttonList) {
            b.drawButton(context, mouseX, mouseY);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(int i = 0; i < 6; i++) {
            String b = BUTTONS[i];
            GuiButton guiButton = buttonList.get(i);
            float x = guiButton.x;
            float y = guiButton.y;

            if(mouseX >= x - guiButton.width / 2.0 && mouseY >= y && mouseX <= x + guiButton.width / 2.0 && mouseY <= y + 60) {
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
