package com.tangykiwi.kiwiclient.gui.mainmenu;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.gui.mainmenu.dummy.DummyClientPlayerEntity;
import com.tangykiwi.kiwiclient.gui.mainmenu.particles.ParticleManager;
import com.tangykiwi.kiwiclient.modules.client.ClickGui;
import com.tangykiwi.kiwiclient.util.render.color.ColorUtil;
import com.tangykiwi.kiwiclient.util.Utils;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class MainMenu extends Screen {

    public final String[] BUTTONS = {"Singleplayer", "Multiplayer", "Realms", "Options", "Language", "Quit"};
    public final ArrayList<GuiButton> buttonList = new ArrayList<GuiButton>();
    public Identifier skin = new Identifier("textures/entity/steve.png");
    public ParticleManager particles;

    public MainMenu() {
        super(Text.translatable("narrator.screen.title"));
    }

    public void init() {
        buttonList.clear();
        particles = new ParticleManager();
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

        MinecraftClient.getInstance().getSkinProvider().loadSkin(new GameProfile(this.client.getSession().getProfile().getId(), "cskin"), (type, identifier, minecraftProfileTexture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                skin = identifier;
            }
        }, false);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);

        RenderSystem.setShaderTexture(0, KiwiClient.MENU);
        this.drawTexture(matrixStack, 0, 0, 20 * mouseX / this.width,  20 * mouseY / this.height, this.width + 20 * mouseX / this.width, this.height + 20 * mouseY / this.height, this.width + 40, this.height + 40);
        this.fillGradient(matrixStack, 0, 0, this.width, this.height, 0x00000000, 0xff000000);

        particles.render(matrixStack, mouseX, mouseY);

        String version = "v" + KiwiClient.version + " - MC " + SharedConstants.getGameVersion().getName();
        DrawableHelper.fill(matrixStack, 0, 0, IFont.CONSOLAS.getStringWidth(version) + 4, IFont.CONSOLAS.getFontHeight() + 2, 0x90000000);
        IFont.CONSOLAS.drawString(matrixStack, version, 1, 2, 0xFFFFFF, 1);

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, new Identifier("kiwiclient:textures/menu/title.png"));
        this.drawTexture(matrixStack, this.width / 2 - 160, this.height / 2 - 55, 0, 0, 320, 40, 320, 40);
        RenderSystem.disableBlend();

        for(GuiButton b : buttonList) {
            b.drawButton(matrixStack, mouseX, mouseY);
        }

        com.tangykiwi.kiwiclient.modules.other.MainMenu mm = (com.tangykiwi.kiwiclient.modules.other.MainMenu) KiwiClient.moduleManager.getModule(com.tangykiwi.kiwiclient.modules.other.MainMenu.class);

        if(mm.getSetting(0).asMode().mode == 0) {
            int renderScale = 2 * this.height / 100;
            RenderSystem.setShaderTexture(0, skin);
            this.drawTexture(matrixStack, 2, this.height - 8 * renderScale - 2, 8 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 64 * renderScale, 64 * renderScale);
            this.drawTexture(matrixStack, 2, this.height - 8 * renderScale - 2, 8 * renderScale, 8 * renderScale, 40 * renderScale, 8 * renderScale, 8 * renderScale, 8 * renderScale, 64 * renderScale, 64 * renderScale);
            IFont.CONSOLAS.drawString(matrixStack, this.client.getSession().getUsername(), 8 * renderScale + 3, this.height - IFont.CONSOLAS.getFontHeight() - 2, ColorUtil.getRainbow(4, 0.8f, 1), 1);
        }
        else {
            ClientPlayerEntity player = DummyClientPlayerEntity.getInstance();
            int height = this.height - 7;
            int playerX = 20;
            InventoryScreen.drawEntity(playerX, height, 30, -mouseX + playerX, -mouseY + height - 30, player);
            IFont.CONSOLAS.drawString(matrixStack, this.client.getSession().getUsername(), 35, this.height - IFont.CONSOLAS.getFontHeight() - 4, ColorUtil.getRainbow(4, 0.8f, 1), 1);
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

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_SEMICOLON) {
            Utils.mc.setScreen(ClickGui.clickGui);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}