package com.tangykiwi.kiwiclient.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsBridgeScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;


public class MainMenu extends Screen {

    public MainMenu() {
        super(new TranslatableText("narrator.screen.title"));
    }

    public void init() {

    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        String[] buttons = {"Singleplayer", "Multiplayer", "Options", "Language", "Quit"};
        TextRenderer textRenderer = this.client.inGameHud.getFontRenderer();
        int offset = 0;
        for(String b : buttons) {
            textRenderer.draw(matrixStack, b, offset * (this.width / buttons.length) + (this.width / buttons.length) / 2 + 8, this.height - 20, -1);
            offset++;
        }

        super.render(matrixStack, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        String[] buttons = {"Singleplayer", "Multiplayer", "Options", "Language", "Quit"};

        int offset = 0;
        for(String b : buttons) {
            float x = offset * (this.width / buttons.length) + (this.width / buttons.length) / 2 + 8 - textRenderer.getWidth(b) / 2f;
            float y = this.height - 20;

            if(mouseX >= x && mouseY >= y && mouseX < x + textRenderer.getWidth(b) && mouseY < y + textRenderer.fontHeight) {
                switch(b) {
                    case "Singleplayer":
                        this.client.openScreen(new SelectWorldScreen(this));
                        break;
                    case "Multiplayer":
                        Screen screen = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
                        this.client.openScreen((Screen)screen);
                        break;
                    case "Options":
                        this.client.openScreen(new OptionsScreen(this, this.client.options));
                        break;
                    case "Language":
                        this.client.openScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
                        break;
                    case "Quit":
                        this.client.scheduleStop();
                        break;
                }
            }

            offset++;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
