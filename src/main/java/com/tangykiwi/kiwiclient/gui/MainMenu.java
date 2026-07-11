package com.tangykiwi.kiwiclient.gui;

import com.mojang.realmsclient.RealmsMainScreen;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.particles.ParticleManager;
import com.tangykiwi.kiwiclient.module.client.ClickGUI;
import com.tangykiwi.kiwiclient.util.Textures;
import com.tangykiwi.kiwiclient.util.font.FontManager;
import com.tangykiwi.kiwiclient.util.font.FontRenderer;
import com.tangykiwi.kiwiclient.util.render.RenderUtils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import java.awt.*;
import java.util.ArrayList;

import static com.tangykiwi.kiwiclient.KiwiClient.moduleManager;

public class MainMenu extends Screen {
    public final String[] BUTTONS = {"Singleplayer", "Multiplayer", "Realms", "Options", "Language", "Quit"};
    public final int[] offset = {-150, -90, -30, 30, 90, 150};
    public final ArrayList<GuiButton> buttonList = new ArrayList<GuiButton>();
    public static FontRenderer fontRenderer;
    public static ParticleManager particleManager;

    public MainMenu() {
        super(Component.translatable("narrator.screen.title"));
    }

    @Override
    public void init() {
        buttonList.clear();
        particleManager = new ParticleManager();

        int initHeight = this.height / 2;
        int xMid = this.width / 2;

        fontRenderer = KiwiClient.fontManager.getSize(8, FontManager.Type.CONSOLAS);
        for (int i = 0; i < BUTTONS.length; i++) {
            buttonList.add(new GuiButton(xMid + offset[i], initHeight, BUTTONS[i]));
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        Identifier menubg = Textures.MENU;
        context.blit(RenderPipelines.GUI_TEXTURED, menubg, 0, 0, 20 * mouseX / this.width,  20 * mouseY / this.height, this.width + 20 * mouseX / this.width, this.height + 20 * mouseY / this.height, this.width + 40, this.height + 40);
        context.fillGradient(0, 0, this.width, this.height, 0x00000000, 0xff000000);

        String version = "v" + KiwiClient.VERSION + " - MC " + KiwiClient.MC_VERSION;
        // RenderUtils.drawRectWH(context, 0, 0, (int) fontRenderer.getStringWidth(version) + 4, (int) fontRenderer.getStringHeight(version) + 2, 0x90000000);
        RenderUtils.drawRoundedQuadWH(context, 0, 0, (int) fontRenderer.getStringWidth(version) + 4, (int) fontRenderer.getStringHeight(version) + 2, 5, 90, 0x90000000);
        fontRenderer.drawString(context, version, 1, 2, new Color(0xFFFFFF));

        particleManager.render(context, mouseX, mouseY);

        context.blit(RenderPipelines.GUI_TEXTURED, Textures.TITLE, this.width / 2 - 160, this.height / 2 - 55, 0, 0, 320, 40, 320, 40);

        for(GuiButton b : buttonList) {
            b.drawButton(context, mouseX, mouseY);
        }

        String username = this.minecraft.getUser().getName();
        fontRenderer.drawString(context, username, 1, this.height - fontRenderer.getStringHeight(username) - 2, RenderUtils.getRainbowColor(4, 0.8f, 1));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();
        for(int i = 0; i < 6; i++) {
            String b = BUTTONS[i];
            GuiButton guiButton = buttonList.get(i);
            float x = guiButton.x;
            float y = guiButton.y;

            if(mouseX >= x - 50 / 2.0 && mouseY >= y && mouseX <= x + 50 / 2.0 && mouseY <= y + 60) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                switch(b) {
                    case "Singleplayer":
                        this.minecraft.setScreenAndShow(new SelectWorldScreen(this));
                        break;
                    case "Multiplayer":
                        Screen screen = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
                        this.minecraft.setScreenAndShow(screen);
                        break;
                    case "Realms":
                        this.minecraft.setScreenAndShow(new RealmsMainScreen(this));
                        break;
                    case "Options":
                        this.minecraft.setScreenAndShow(new OptionsScreen(this, this.minecraft.options, false));
                        break;
                    case "Language":
                        this.minecraft.setScreenAndShow(new LanguageSelectScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
                        break;
                    case "Quit":
                        this.minecraft.stop();
                        break;
                }
            }
        }

        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyEvent keyInput) {
        int keyCode = keyInput.key();
        ClickGUI clickGUI = (ClickGUI) moduleManager.getModule(ClickGUI.class);
        if(keyCode == clickGUI.getKeyCode()) {
            clickGUI.onEnable();
        }

        return super.keyPressed(keyInput);
    }

    static class GuiButton {
        private int index = 0;
        public int x, y;
        public String buttonText;
        private boolean movingUp = false;
        private final Identifier icon;

        public GuiButton(int x, int y, String buttonText) {
            this.x = x;
            this.y = y;
            this.buttonText = buttonText;
            icon = Identifier.parse("kiwiclient:textures/menu/" + buttonText.toLowerCase() + ".png");
        }

        public void drawButton(GuiGraphicsExtractor context, int mouseX, int mouseY) {
            boolean hovered = mouseX >= x - 50 / 2 && mouseY >= y && mouseX <= x + 50 / 2 && mouseY <= y + 60;
            if (hovered && index < 1) {
                movingUp = true;
            } else if (index == 14 && movingUp && !hovered) {
                movingUp = false;
            }

            if (movingUp && index < 14) {
                index++;
            } else if (index > 0 && !movingUp) {
                index--;
            }

            fontRenderer.drawCenteredString(context, buttonText, x, y + getPosition(index) + 55, hovered ? RenderUtils.getRainbowColor(3, 0.8f, 1) : new Color(-1));

            context.blit(RenderPipelines.GUI_TEXTURED, icon, (x - 50 / 2), (int) (y + getPosition(index)), 0, 0, 50, 50, 50, 50);
        }

        public float getPosition(int index) {
            if (movingUp) {
                return new float[]{0, -48.839F, -107.135F, -147.163F, -159.884F, -148.736F,
                        -128.329F,
                        -112.506F,
                        -107.611F,
                        -117.462F,
                        -123.848F,
                        -118.805F,
                        -120.371F,
                        -119.885F,
                        -120}[index] / 10;
            } else {
                return new float[]{0,
                        -0.115F,
                        0.371F,
                        -1.195F,
                        3.848F,
                        -2.538F,
                        -12.389F,
                        -7.494F,
                        8.329F,
                        28.736F,
                        39.884F,
                        27.163F,
                        -12.865F,
                        -71.161F,
                        -120}[index] / 10;
            }
        }
    }
}


