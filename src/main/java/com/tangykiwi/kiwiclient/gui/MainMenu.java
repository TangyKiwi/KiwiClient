package com.tangykiwi.kiwiclient.gui;

import com.tangykiwi.kiwiclient.util.ColorUtil;
import com.tangykiwi.kiwiclient.util.GlyphPage;
import com.tangykiwi.kiwiclient.util.GlyphPageFontRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.awt.*;

public class MainMenu extends Screen {

    public final String[] BUTTONS = {"Singleplayer", "Multiplayer", "Options", "Language", "Quit"};
    public MainMenu() {
        super(new TranslatableText("narrator.screen.title"));
    }

    public void init() {

    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        TextureManager textureManager = this.client.getTextureManager();
        textureManager.bindTexture(new Identifier("kiwiclient:background1.jpg"));
        this.drawTexture(matrixStack, 0, 0, 0, 0, this.width, this.height, this.width, this.height);
        this.fillGradient(matrixStack, 0, this.height - 100, this.width, this.height, 0x00000000, 0xff000000);

        //Style CUSTOM_STYLE = Style.EMPTY.withFont(new Identifier("kiwiclient", "titillium"));
        TextRenderer textRenderer = this.client.textRenderer;
        int offset = 0;
        for(String b : BUTTONS) {
            float x = offset * (this.width / BUTTONS.length) + (this.width / BUTTONS.length) / 3;
            float y = this.height - 20;

            boolean hovered = (mouseX >= x && mouseY >= y && mouseX < x + textRenderer.getWidth(b) && mouseY < y + textRenderer.fontHeight);
            textRenderer.draw(matrixStack, b, offset * (this.width / BUTTONS.length) + (this.width / BUTTONS.length) / 3, this.height - 20, hovered ? ColorUtil.getRainbow(3, 0.8f, 1) : -1);
            offset++;
        }

        /**
        char[] chars = new char[256];

        for(int i = 0; i < 256; i++) {
            chars[i] = (char) i;
        }

        GlyphPage glyphPage = new GlyphPage(new Font("Arial", Font.PLAIN, 80), true, true);

        glyphPage.generateGlyphPage(chars);
        glyphPage.setupTexture();

        GlyphPageFontRenderer glyphPageFontRenderer = new GlyphPageFontRenderer(glyphPage, glyphPage, glyphPage, glyphPage);
        glyphPageFontRenderer.drawString("TEST", 70, 70, 1, false);
         **/

        super.render(matrixStack, mouseX, mouseY, delta);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int offset = 0;
        for(String b : BUTTONS) {
            float x = offset * (this.width / BUTTONS.length) + (this.width / BUTTONS.length) / 3;
            float y = this.height - 20;

            if(mouseX >= x && mouseY >= y && mouseX < x + textRenderer.getWidth(b) && mouseY < y + textRenderer.fontHeight) {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
