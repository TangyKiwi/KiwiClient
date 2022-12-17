package com.tangykiwi.kiwiclient.gui;

import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.stream.Collectors;

public class AccountButtonWidget extends TexturedButtonWidget
{
    private final @Nullable Screen screen;
    public static Identifier texture = new Identifier("kiwiclient:textures/menu/widgets.png");

    public AccountButtonWidget(Screen screen, int x, int y, PressAction pressAction)
    {
        super(x, y, 20, 20, 0, 146, 20, ButtonWidget.WIDGETS_TEXTURE, 256, 256, pressAction, Text.literal("Account Manager"));
        this.screen = screen;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        // Cascade the rendering
        super.renderButton(matrices, mouseX, mouseY, delta);

        // Render the current session status
        RenderSystem.setShaderTexture(0, texture);
        int u = 16;
        if(AccountManagerScreen.accounts != null && AccountManagerScreen.accounts.stream().map(AccountManagerScreen.Account::getUUID).collect(Collectors.toList()).contains(Utils.mc.getSession().getUuid())) {
            for(AccountManagerScreen.Account a : AccountManagerScreen.accounts) {
                if(a.uuid.equals(Utils.mc.getSession().getUuid())) {
                    if(a.type == AccountManagerScreen.AccountType.CRACKED) {
                        u = 8;
                    } else {
                        u = 0;
                    }
                    break;
                }
            }
        }
        drawTexture(matrices, getX() + width - 6, getY() - 1, u, 60, 8, 8, 128, 128);
    }
}
