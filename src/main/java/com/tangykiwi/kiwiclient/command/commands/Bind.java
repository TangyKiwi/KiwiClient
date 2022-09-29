package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.KiwiClient;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.command.argument.ModuleArgumentType;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Bind extends Command {
    public Bind() {
        super("bind", "Binds a module to a specified key", "b");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.module()).executes(context -> {
                Module m = ModuleArgumentType.getModule(context, "module");
                KiwiClient.moduleManager.module = m;
                return SINGLE_SUCCESS;
            })
            .then(argument("key", StringArgumentType.greedyString()).executes(context -> {
                int key = -1;
                String keycode = context.getArgument("key", String.class);

                try {
                    key = InputUtil.fromTranslationKey("key.keyboard." + keycode.toLowerCase()).getCode();
                } catch (IllegalArgumentException e) {
                    if (keycode.toLowerCase().startsWith("right")) {
                        try {
                            key = InputUtil.fromTranslationKey("key.keyboard." + keycode.replaceFirst("right", "right.")).getCode();
                        } catch (IllegalArgumentException e1) {
                            addMessage("Unknown key: §c" + keycode + " / " + keycode.toLowerCase().replaceFirst("right", "right."));
                            return SINGLE_SUCCESS;
                        }
                    } else if (keycode.toLowerCase().startsWith("r")) {
                        try {
                            key = InputUtil.fromTranslationKey("key.keyboard." + keycode.toLowerCase().replaceFirst("r", "right.")).getCode();
                        } catch (IllegalArgumentException e1) {
                            addMessage("Unknown key: §c" + keycode + " / " + keycode.toLowerCase().replaceFirst("r", "right."));
                            return SINGLE_SUCCESS;
                        }
                    } else if (keycode.toLowerCase().startsWith("left")) {
                        try {
                            key = InputUtil.fromTranslationKey("key.keyboard." + keycode.replaceFirst("left", "left.")).getCode();
                        } catch (IllegalArgumentException e1) {
                            addMessage("Unknown key: §c" + keycode + " / " + keycode.toLowerCase().replaceFirst("left", "left."));
                            return SINGLE_SUCCESS;
                        }
                    } else if (keycode.toLowerCase().startsWith("l")) {
                        try {
                            key = InputUtil.fromTranslationKey("key.keyboard." + keycode.toLowerCase().replaceFirst("l", "left.")).getCode();
                        } catch (IllegalArgumentException e1) {
                            addMessage("Unknown key: §c" + keycode + " / " + keycode.toLowerCase().replaceFirst("l", "left."));
                            return SINGLE_SUCCESS;
                        }
                    }
                    else if (keycode.equals("`") || keycode.equals("~")) {
                        key = GLFW.GLFW_KEY_GRAVE_ACCENT;
                    } else if (keycode.equals("-") || keycode.equals("_")) {
                        key = GLFW.GLFW_KEY_MINUS;
                    } else if (keycode.equals("=") || keycode.equals("+")) {
                        key = GLFW.GLFW_KEY_EQUAL;
                    } else if (keycode.equals("[") || keycode.equals("{")) {
                        key = GLFW.GLFW_KEY_RIGHT_BRACKET;
                    } else if (keycode.equals("]") || keycode.equals("}")) {
                        key = GLFW.GLFW_KEY_LEFT_BRACKET;
                    } else if (keycode.equals("\\") || keycode.equals("|")) {
                        key = GLFW.GLFW_KEY_BACKSLASH;
                    } else if (keycode.equals(";") || keycode.equals(":")) {
                        key = GLFW.GLFW_KEY_SEMICOLON;
                    } else if (keycode.equals("'") || keycode.equals("\"")) {
                        key = GLFW.GLFW_KEY_APOSTROPHE;
                    } else if (keycode.equals(",") || keycode.equals("<")) {
                        key = GLFW.GLFW_KEY_COMMA;
                    } else if (keycode.equals(".") || keycode.equals(">")) {
                        key = GLFW.GLFW_KEY_PERIOD;
                    } else if (keycode.equals("/") || keycode.equals("?")) {
                        key = GLFW.GLFW_KEY_SLASH;
                    } else {
                        addMessage("Unknown key: §c" + keycode);
                        return SINGLE_SUCCESS;
                    }
                }

                Module m = ModuleArgumentType.getModule(context, "module");
                m.setKeyCode(key);
                m.getSetting(m.getSettings().size() - 1).setDataValue(m.getKeyCode());
                addMessage("Bound §d" + m.getName() + "§r to §a" + keycode + " (KEY" + key + ")");
                return SINGLE_SUCCESS;
            }))
        );
    }
}
