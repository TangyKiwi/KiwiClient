package com.tangykiwi.kiwiclient.gui.window.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class WindowTextFieldWidget extends WindowWidget {

    public TextFieldWidget textField;

    public WindowTextFieldWidget(int x, int y, int width, int height, String text) {
        super(x, y, x + width, y + height);
        this.textField = new TextFieldWidget(mc.textRenderer, x, y, width, height, Text.empty());
        this.textField.setText(text);
        this.textField.setMaxLength(32767);
    }

    protected WindowTextFieldWidget(int x, int y, int width, int height) {
        super(x, y, x + width, y + height);
    }

    @Override
    public void render(DrawContext context, int windowX, int windowY, int mouseX, int mouseY) {
        textField.setX(windowX + x1);
        textField.setY(windowY + y1);
        textField.render(context, mouseX, mouseY, MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true));

        super.render(context, windowX, windowY, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int windowX, int windowY, int mouseX, int mouseY, int button) {
        super.mouseClicked(windowX, windowY, mouseX, mouseY, button);

        textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        super.charTyped(chr, modifiers);

        textField.charTyped(chr, modifiers);
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);

        textField.keyPressed(keyCode, scanCode, modifiers);
    }
}
