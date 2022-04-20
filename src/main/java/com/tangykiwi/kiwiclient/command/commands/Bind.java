package com.tangykiwi.kiwiclient.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tangykiwi.kiwiclient.command.Command;
import com.tangykiwi.kiwiclient.command.argument.ModuleArgumentType;
import com.tangykiwi.kiwiclient.modules.Module;
import com.tangykiwi.kiwiclient.modules.ModuleManager;
import com.tangykiwi.kiwiclient.util.Utils;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class Bind extends Command {
    public Bind() {
        super("bind", "Binds a module to a specified key", "b");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.module())
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
                            Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + keycode + " / " + keycode.toLowerCase().replaceFirst("right", "right.")));
                            return SINGLE_SUCCESS;
                        }
                    } else if (keycode.toLowerCase().startsWith("r")) {
                        try {
                            key = InputUtil.fromTranslationKey("key.keyboard." + keycode.toLowerCase().replaceFirst("r", "right.")).getCode();
                        } catch (IllegalArgumentException e1) {
                            Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + keycode + " / " + keycode.toLowerCase().replaceFirst("r", "right.")));
                            return SINGLE_SUCCESS;
                        }
                    } else if (keycode.toLowerCase().startsWith("left")) {
                        try {
                            key = InputUtil.fromTranslationKey("key.keyboard." + keycode.replaceFirst("left", "left.")).getCode();
                        } catch (IllegalArgumentException e1) {
                            Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + keycode + " / " + keycode.toLowerCase().replaceFirst("left", "left.")));
                            return SINGLE_SUCCESS;
                        }
                    } else if (keycode.toLowerCase().startsWith("l")) {
                        try {
                            key = InputUtil.fromTranslationKey("key.keyboard." + keycode.toLowerCase().replaceFirst("l", "left.")).getCode();
                        } catch (IllegalArgumentException e1) {
                            Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + keycode + " / " + keycode.toLowerCase().replaceFirst("l", "left.")));
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
                        Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + keycode));
                        return SINGLE_SUCCESS;
                    }
                }

                Module m = ModuleArgumentType.getModule(context, "module");
                m.setKeyCode(key);
                m.getSetting(m.getSettings().size() - 1).setDataValue(m.getKeyCode());
                Utils.mc.inGameHud.getChatHud().addMessage(new LiteralText("Bound " + m.getName() + " to " + keycode + " (KEY" + key + ")"));
                return SINGLE_SUCCESS;
            }))
        );
    }
//
//    @Override
//    public String[] getAliases() {
//        return new String[]{"bind", "b"};
//    }
//
//    @Override
//    public String getDescription() {
//        return "Binds a module to a key.";
//    }
//
//    @Override
//    public String getSyntax() {
//        return ".bind [module] [key]";
//    }
//
//    @Override
//    public void onCommand(String command, String[] args) throws Exception {
//        if(args.length >= 2) {
//            for(Module m : ModuleManager.moduleList) {
//                if(m.getName().equalsIgnoreCase(args[0])) {
//                    int key = -1;
//
//                    try {
//                        key = InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase()).getCode();
//                    } catch (IllegalArgumentException e) {
//                        if (args[1].toLowerCase().startsWith("right")) {
//                            try {
//                                key = InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase().replaceFirst("right", "right.")).getCode();
//                            } catch (IllegalArgumentException e1) {
//                                mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + args[1] + " / " + args[1].toLowerCase().replaceFirst("right", "right.")));
//                                return;
//                            }
//                        } else if (args[1].toLowerCase().startsWith("r")) {
//                            try {
//                                key = InputUtil.fromTranslationKey("key.keyboard." + args[1].toLowerCase().replaceFirst("r", "right.")).getCode();
//                            } catch (IllegalArgumentException e1) {
//                                mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + args[1] + " / " + args[1].toLowerCase().replaceFirst("r", "right.")));
//                                return;
//                            }
//                        } else if (args[1].equals("`") || args[1].equals("~")) {
//                            key = GLFW.GLFW_KEY_GRAVE_ACCENT;
//                        } else if (args[1].equals("-") || args[1].equals("_")) {
//                            key = GLFW.GLFW_KEY_MINUS;
//                        } else if (args[1].equals("=") || args[1].equals("+")) {
//                            key = GLFW.GLFW_KEY_EQUAL;
//                        } else if (args[1].equals("[") || args[1].equals("{")) {
//                            key = GLFW.GLFW_KEY_RIGHT_BRACKET;
//                        } else if (args[1].equals("]") || args[1].equals("}")) {
//                            key = GLFW.GLFW_KEY_LEFT_BRACKET;
//                        } else if (args[1].equals("\\") || args[1].equals("|")) {
//                            key = GLFW.GLFW_KEY_BACKSLASH;
//                        } else if (args[1].equals(";") || args[1].equals(":")) {
//                            key = GLFW.GLFW_KEY_SEMICOLON;
//                        } else if (args[1].equals("'") || args[1].equals("\"")) {
//                            key = GLFW.GLFW_KEY_APOSTROPHE;
//                        } else if (args[1].equals(",") || args[1].equals("<")) {
//                            key = GLFW.GLFW_KEY_COMMA;
//                        } else if (args[1].equals(".") || args[1].equals(">")) {
//                            key = GLFW.GLFW_KEY_PERIOD;
//                        } else if (args[1].equals("/") || args[1].equals("?")) {
//                            key = GLFW.GLFW_KEY_SLASH;
//                        } else {
//                            mc.inGameHud.getChatHud().addMessage(new LiteralText("Unknown key: " + args[1]));
//                            return;
//                        }
//                    }
//
//                    m.setKeyCode(key);
//                    mc.inGameHud.getChatHud().addMessage(new LiteralText("Bound " + m.getName() + " to " + args[1] + " (KEY" + key + ")"));
//                    return;
//                }
//            }
//            mc.inGameHud.getChatHud().addMessage(new LiteralText("Module \"" + args[0] + "\" Not Found!"));
//        }
//        else mc.inGameHud.getChatHud().addMessage(new LiteralText(getSyntax()));
//    }
}
