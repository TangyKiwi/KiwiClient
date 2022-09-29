package com.tangykiwi.kiwiclient.gui;

import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.Utils;
import com.tangykiwi.kiwiclient.util.font.IFont;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.lang.reflect.Field;

public class BindScreen extends Screen {
    Module module;
    long closeAt = -1;

    public BindScreen(Module module) {
        super(Text.of(""));
        this.module = module;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Utils.mc.keyboard.setRepeatEvents(true);
        this.renderBackground(matrices);

        IFont.CONSOLAS.drawCenteredString(matrices, "Press a Key", width / 2D, height / 2D - IFont.CONSOLAS.getFontHeight(), Color.WHITE.getRGB(), 1);
        IFont.CONSOLAS.drawCenteredString(matrices, "ESC to quit / DEL to remove bind", width / 2D, height / 2D, Color.WHITE.getRGB(), 1);
        String kn = module.getKeyCode() > 0 ? GLFW.glfwGetKeyName(module.getKeyCode(), GLFW.glfwGetKeyScancode(module.getKeyCode())) : "...";
        if (kn == null) {
            try {
                for (Field declaredField : GLFW.class.getDeclaredFields()) {
                    if (declaredField.getName().startsWith("GLFW_KEY_")) {
                        int a = (int) declaredField.get(null);
                        if (a == this.module.getKeyCode()) {
                            String nb = declaredField.getName().substring("GLFW_KEY_".length());
                            kn = nb.substring(0, 1).toUpperCase() + nb.substring(1).toLowerCase();
                        }
                    }
                }
            } catch (Exception ignored) {
                kn = "Unknown: " + module.getKeyCode();
            }
        }
        IFont.CONSOLAS.drawCenteredString(matrices, "Current Key: " + kn, width / 2D, height / 2D  + IFont.CONSOLAS.getFontHeight(), Color.WHITE.getRGB(), 1);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int tempKeyCode = keyCode;
        if (closeAt != -1) {
            return false;
        }
        if (tempKeyCode == GLFW.GLFW_KEY_ESCAPE) {
            tempKeyCode = module.getKeyCode();
        }
        else if (tempKeyCode == GLFW.GLFW_KEY_DELETE) {
            tempKeyCode = -1;
        }
        module.setKeyCode(tempKeyCode);
        module.getSetting(module.getSettings().size() - 1).setDataValue(tempKeyCode);
        closeAt = System.currentTimeMillis() + 500;
        return true;
    }

    @Override
    public void tick() {
        if (closeAt != -1 && closeAt < System.currentTimeMillis()) {
            String prefix = "§a[§6KiwiClient§a]§r";
            String text = "Unbound §d" + module.getName();
            if(module.getKeyCode() != -2) {
                text = "Bound §d" + module.getName() + "§r to §a" + InputUtil.fromKeyCode(module.getKeyCode(), -1).getLocalizedText().getString() + " (KEY" + module.getKeyCode() + ")";
            }
            Utils.mc.inGameHud.getChatHud().addMessage(Text.literal(prefix + " " + text));
            close();
        }
        super.tick();
    }
}
