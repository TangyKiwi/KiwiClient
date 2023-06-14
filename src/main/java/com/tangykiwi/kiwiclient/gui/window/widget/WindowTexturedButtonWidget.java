package com.tangykiwi.kiwiclient.gui.window.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class WindowTexturedButtonWidget extends WindowWidget {

    public Identifier texture;
    public Runnable action;
    public int loc;

    public WindowTexturedButtonWidget(int x1, int y1, int x2, int y2, Identifier texture, Runnable action, int loc) {
        super(x1, y1, x2, y2);
        this.texture = texture;
        this.action = action;
        this.loc = loc;
    }

    @Override
    public void render(DrawContext context, int windowX, int windowY, int mouseX, int mouseY) {
        super.render(context, windowX, windowY, mouseX, mouseY);

        int bx1 = windowX + x1;
        int by1 = windowY + y1;

        int v = 0;
        if(isInBounds(windowX, windowY, mouseX, mouseY)) {
            v += 20;
        }

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        context.drawTexture(texture, bx1, by1, loc, v, 20, 20, 128, 128);
    }

    @Override
    public void mouseClicked(int windowX, int windowY, int mouseX, int mouseY, int button) {
        super.mouseClicked(windowX, windowY, mouseX, mouseY, button);

        if (isInBounds(windowX, windowY, mouseX, mouseY)) {
            action.run();
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }
}
